package com.echomenswear.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOtpEmail(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("ikramshemse@gmail.com");
        message.setTo(toEmail);
        message.setSubject("Echo Menswear - Password Reset Code");
        message.setText("Your password reset code is: " + otp + "\n\nThis code will expire in 10 minutes. If you did not request this, please ignore this email.");
        
        mailSender.send(message);
    }
}