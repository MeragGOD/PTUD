package com.carrental.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:noreply@carrental.com}")
    private String fromEmail;

    @Value("${app.email.enabled:false}")
    private boolean emailEnabled;

    @Async
    public void sendPasswordResetEmail(String to, String resetLink) {
        if (!emailEnabled) {
            log.info("Email sending is disabled. Would have sent password reset email to: {}", to);
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject("Password Reset Request");

            String emailContent =
                    "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;'>" +
                            "<h2 style='color: #3b82f6;'>Password Reset Request</h2>" +
                            "<p>Hello,</p>" +
                            "<p>We received a request to reset your password. Click the link below to set a new password:</p>" +
                            "<p><a href='" + resetLink + "' style='display: inline-block; background-color: #3b82f6; color: white; padding: 10px 20px; text-decoration: none; border-radius: 4px;'>Reset Password</a></p>" +
                            "<p>If you didn't request a password reset, please ignore this email.</p>" +
                            "<p>This link will expire in 30 minutes.</p>" +
                            "<p>Thank you,<br>Car Rental Team</p>" +
                            "</div>";

            helper.setText(emailContent, true);

            mailSender.send(message);
            log.info("Password reset email sent to: {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send password reset email to: {}", to, e);
        }
    }

    @Async
    public void sendBookingConfirmationEmail(String to, String bookingDetails) {
        if (!emailEnabled) {
            log.info("Email sending is disabled. Would have sent booking confirmation email to: {}", to);
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject("Booking Confirmation");

            String emailContent =
                    "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;'>" +
                            "<h2 style='color: #3b82f6;'>Booking Confirmation</h2>" +
                            "<p>Hello,</p>" +
                            "<p>Thank you for your booking with Car Rental. Here are your booking details:</p>" +
                            bookingDetails +
                            "<p>If you have any questions, please contact our customer service.</p>" +
                            "<p>Thank you,<br>Car Rental Team</p>" +
                            "</div>";

            helper.setText(emailContent, true);

            mailSender.send(message);
            log.info("Booking confirmation email sent to: {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send booking confirmation email to: {}", to, e);
        }
    }
}
