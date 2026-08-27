package com.echomenswear.controller;

import com.echomenswear.dto.AuthResponse;
import com.echomenswear.dto.LoginRequest;
import com.echomenswear.dto.RegisterRequest;
import com.echomenswear.dto.PasswordRequests.*;
import com.echomenswear.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            return ResponseEntity.ok(userService.login(request));
        } catch (Exception e) {
            // This will catch the lockout message and return a clean 401 response instead of a 500 crash!
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            return ResponseEntity.ok(userService.register(request));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    // ==========================================
    // PASSWORD RECOVERY & UPDATES
    // ==========================================

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        userService.processForgotPassword(request);
        return ResponseEntity.ok("If your email is registered, you will receive a reset code shortly.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            userService.resetPassword(request);
            return ResponseEntity.ok("Password successfully reset.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/change-password")
    @PreAuthorize("isAuthenticated()") 
    public ResponseEntity<?> changePassword(Principal principal, @RequestBody ChangePasswordRequest request) {
        try {
            userService.changePassword(principal.getName(), request);
            return ResponseEntity.ok("Password updated successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}