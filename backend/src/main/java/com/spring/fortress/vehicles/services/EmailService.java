package com.spring.fortress.vehicles.services;

import com.spring.fortress.vehicles.dtos.request.AccountActivationRequest;
import com.spring.fortress.vehicles.dtos.request.ResetPasswordRequest;
import com.spring.fortress.vehicles.dtos.response.VerificationResponse;
import com.spring.fortress.vehicles.models.Vehicle;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * Service for sending email notifications in the vehicle tracking system.
 * Handles password reset, account activation, verification, and transfer notifications.
 *
 * @author Fortress Backend
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${reset-password-url}")
    private String resetPasswordUrl;

    @Value("${support-email}")
    private String supportEmail;

    @Value("${spring.mail.username}")
    private String fromEmail;

    /**
     * Generates a common email signature with support contact and copyright.
     *
     * @return the HTML signature
     */
    private String getCommonSignature() {
        return "<br><br>If you need help, contact us at: <a href='mailto:" + supportEmail + "'>" + supportEmail + "</a><br>© " + LocalDate.now().getYear();
    }

    /**
     * Sends a password reset email with a reset code and link.
     *
     * @param request the reset password request
     * @throws IllegalStateException if email sending fails
     */
    public void sendResetPasswordMail(ResetPasswordRequest request) {
        log.info("Sending password reset email to: {}", request.email());
        String subject = "Password Reset Request";
        String html = "<p>Dear " + request.fullName() + ",</p>"
                + "<p>You requested to reset your password. Use the following code:</p>"
                + "<h2>" + request.resetCode() + "</h2>"
                + "<p>Or click <a href='" + resetPasswordUrl + "'>here</a> to reset your password.</p>"
                + getCommonSignature();
        sendEmail(request.email(), subject, html);
    }

    /**
     * Sends an account activation email with a verification code.
     *
     * @param request the account activation request
     * @throws IllegalStateException if email sending fails
     */
    public void sendActivateAccountEmail(AccountActivationRequest request) {
        log.info("Sending account activation email to: {}", request.email());
        String subject = "Account Activation Request";
        String html = "<p>Hello " + request.fullName() + ",</p>"
                + "<p>Please use the following code to activate your account:</p>"
                + "<h2>" + request.verificationCode() + "</h2>"
                + "<p><strong>This code will expire at: " + request.expiresAt() + "</strong></p>"
                + "<p>If your code expires, you can request a new one from the activation page.</p>"
                + getCommonSignature();
        sendEmail(request.email(), subject, html);
    }

    /**
     * Sends a confirmation email after successful account verification.
     *
     * @param response the verification response
     * @throws IllegalStateException if email sending fails
     */
    public void sendAccountVerifiedSuccessfullyEmail(VerificationResponse response) {
        log.info("Sending account verification success email to: {}", response.email());
        String subject = "Account Verification Successful";
        String html = "<p>Hi " + response.fullName() + ",</p>"
                + "<p>Your account has been verified successfully. Welcome aboard!</p>"
                + "<p>You can now log in to your account and start using our vehicle tracking system.</p>"
                + getCommonSignature();
        sendEmail(response.email(), subject, html);
    }

    /**
     * Sends a confirmation email after successful password reset.
     *
     * @param response the verification response
     * @throws IllegalStateException if email sending fails
     */
    public void sendPasswordResetSuccessfully(VerificationResponse response) {
        log.info("Sending password reset success email to: {}", response.email());
        String subject = "Password Reset Successful";
        String html = "<p>Hello " + response.fullName() + ",</p>"
                + "<p>Your password has been reset successfully.</p>"
                + getCommonSignature();
        sendEmail(response.email(), subject, html);
    }

    /**
     * Sends a notification email when a verification code expires.
     *
     * @param email     the recipient's email
     * @param fullName  the recipient's full name
     * @throws IllegalStateException if email sending fails
     */
    public void sendOtpExpiredNotification(String email, String fullName) {
        log.info("Sending OTP expired notification to: {}", email);
        String subject = "Verification Code Expired";
        String html = "<p>Hello " + fullName + ",</p>"
                + "<p>Your verification code has expired.</p>"
                + "<p>Please request a new code by visiting the activation page.</p>"
                + getCommonSignature();
        sendEmail(email, subject, html);
    }

    /**
     * Sends ownership transfer notifications to old and new owners.
     *
     * @param oldOwnerEmail the email of the old owner
     * @param newOwnerEmail the email of the new owner
     * @param vehicle       the transferred vehicle
     * @throws IllegalStateException if email sending fails
     */
    public void sendTransferNotification(String oldOwnerEmail, String newOwnerEmail, Vehicle vehicle) {
        log.info("Sending transfer notifications for vehicle chassis: {}", vehicle.getChassisNumber());
        String subject = "Vehicle Ownership Transfer Notification";

        String oldOwnerMessage = String.format(
                "<p>Dear Customer,</p>" +
                        "<p>This is to inform you that your vehicle (Chassis Number: %s, Model: %s) " +
                        "has been successfully transferred to a new owner.</p>" +
                        "<p>Thank you for using our services.</p>" +
                        "<p>Rwanda Revenue Authority.</p>" +
                        getCommonSignature(),
                vehicle.getChassisNumber(), vehicle.getModelName()
        );

        String newOwnerMessage = String.format(
                "<p>Dear Customer,</p>" +
                        "<p>Congratulations! You are now the new owner of the vehicle " +
                        "(Chassis Number: %s, Model: %s).</p>" +
                        "<p>Please ensure to complete any remaining formalities if required.</p>" +
                        "<p>Thank you for choosing our services.</p>" +
                        "<p>Rwanda Revenue Authority.</p>" +
                        getCommonSignature(),
                vehicle.getChassisNumber(), vehicle.getModelName()
        );

        sendEmail(oldOwnerEmail, subject, oldOwnerMessage);
        sendEmail(newOwnerEmail, subject, newOwnerMessage);
    }

    /**
     * Sends an HTML email to the specified recipient.
     *
     * @param to           the recipient's email address
     * @param subject      the email subject
     * @param htmlContent  the HTML content of the email
     * @throws IllegalStateException if email sending fails
     */
    private void sendEmail(String to, String subject, String htmlContent) {
        if (to == null || to.isBlank()) {
            log.error("Cannot send email: recipient address is null or empty");
            throw new IllegalStateException("Recipient email address is required");
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            mailSender.send(message);
            log.info("Successfully sent email to: {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage(), e);
            throw new IllegalStateException("Failed to send email: " + e.getMessage(), e);
        }
    }
}