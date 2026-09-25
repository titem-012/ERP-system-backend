package com.echomenswear.service;

import com.echomenswear.config.JwtService;
import com.echomenswear.dto.AuthResponse;
import com.echomenswear.dto.LoginRequest;
import com.echomenswear.dto.RegisterRequest;
import com.echomenswear.dto.PasswordRequests.*;
import com.echomenswear.model.User;
import com.echomenswear.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authManager;
    private final EmailService emailService; // Injected for sending OTPs

    public AuthResponse login(LoginRequest request) {
        // 1. Find the user first to check their lock status
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        // 2. Check if the account is currently locked
        if (user.getLockTime() != null) {
            // If current time is before the 10-minute penalty expires, block them
            if (user.getLockTime().plusMinutes(10).isAfter(LocalDateTime.now())) {
                throw new RuntimeException("Account is locked due to too many failed attempts. Try again in 10 minutes.");
            } else {
                // The 10 minutes have passed! Reset the penalty.
                user.setFailedAttemptCount(0);
                user.setLockTime(null);
                userRepository.save(user);
            }
        }

        // 3. Manually check the password to count failures
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            int attempts = user.getFailedAttemptCount() + 1;
            user.setFailedAttemptCount(attempts);

            if (attempts >= 5) {
                user.setLockTime(LocalDateTime.now());
                userRepository.save(user);
                throw new RuntimeException("Account is locked due to 5 failed attempts. Try again in 10 minutes.");
            }

            userRepository.save(user);
            throw new RuntimeException("Invalid username or password. Attempt " + attempts + " of 5.");
        }

        // 4. Password is correct! Reset the failed attempts to 0
        user.setFailedAttemptCount(0);
        user.setLockTime(null);
        userRepository.save(user);

        // 5. Authenticate via Spring Security and Generate Token
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtService.generateToken(userDetails);
        
        return new AuthResponse(token, user);
    }

    public AuthResponse register(RegisterRequest request) {
        boolean isFirst = userRepository.count() == 0;
       // if (!isFirst) {
           // throw new RuntimeException("Registration is closed. Please contact the admin.");
      //  }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setRole("ADMIN");
        user.setAssignedStore("main"); // admin sees both stores
        user.setCreatedAt(LocalDateTime.now());
        user = userRepository.save(user);

        // Generate JWT token so user is logged in immediately after registration
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities("ROLE_ADMIN")
                .build();
        String token = jwtService.generateToken(userDetails);

        return new AuthResponse(token, user);
    }

    public User createCashier(RegisterRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setRole("CASHIER");
        user.setAssignedStore(request.getAssignedStore()); // must be "main" or "sub"
        user.setCreatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    public List<User> getAllCashiers() {
        return userRepository.findByRole("CASHIER");
    }

    public void deleteUser(String id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(id);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // ==========================================
    // PASSWORD RECOVERY & UPDATES
    // ==========================================

    public void processForgotPassword(ForgotPasswordRequest request) {
        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
            // Generate a 6-digit OTP
            String otp = String.format("%06d", new Random().nextInt(999999));
            
            user.setResetOtp(otp);
            user.setOtpExpiry(LocalDateTime.now().plusMinutes(10)); // Expires in 10 minutes
            userRepository.save(user);
            
            emailService.sendOtpEmail(user.getEmail(), otp);
        });
    }

    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid request"));

        if (user.getResetOtp() == null || !user.getResetOtp().equals(request.getOtp())) {
            throw new RuntimeException("Invalid OTP code.");
        }

        if (user.getOtpExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP code has expired. Please request a new one.");
        }

        // Apply new password and clear OTP cache
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setResetOtp(null);
        user.setOtpExpiry(null);
        
        // Also unlock the account if they successfully reset their password
        user.setFailedAttemptCount(0);
        user.setLockTime(null);
        
        userRepository.save(user);
    }

    public void changePassword(String currentUsername, ChangePasswordRequest request) {
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Incorrect current password.");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}
