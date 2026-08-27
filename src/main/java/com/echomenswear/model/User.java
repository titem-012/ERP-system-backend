package com.echomenswear.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "users")
@Data
public class User {
    @Id
    private String id;
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String role;  // "ADMIN" or "CASHIER"
    private String assignedStore; // "main" or "sub"
    private LocalDateTime createdAt;

    // FIELDS FOR OTP PASSWORD RESET
    private String resetOtp;
    private LocalDateTime otpExpiry;

    // NEW FIELDS FOR ACCOUNT LOCKOUT
    private int failedAttemptCount = 0;
    private LocalDateTime lockTime;
}