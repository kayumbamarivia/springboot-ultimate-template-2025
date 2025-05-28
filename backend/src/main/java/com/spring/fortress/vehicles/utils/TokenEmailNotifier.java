package com.spring.fortress.vehicles.utils;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Component for sending token expiration notification emails using Spring Mail and Thymeleaf.
 * Configures and sends HTML emails with token expiration details and recommendations.
 *
 * @author Fortress Backend
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TokenEmailNotifier {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    /**
     * Sends an HTML email notifying the user of expired electricity tokens.
     *
     * @param toEmail       the recipient's email address
     * @param userName      the user's name for personalization
     * @param expiredTokens list of expired tokens to include in the email
     * @throws MessagingException if email sending fails
     * @throws IllegalArgumentException if toEmail or userName is null or empty
     */
    public void sendTokenExpirationEmail(String toEmail, String userName, List<String> expiredTokens) throws MessagingException {
        if (toEmail == null || toEmail.trim().isEmpty()) {
            throw new IllegalArgumentException("Recipient email cannot be null or empty");
        }
        if (userName == null || userName.trim().isEmpty()) {
            throw new IllegalArgumentException("User name cannot be null or empty");
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Electricity Token Expiration Notice");

            Context context = new Context();
            context.setVariable("userName", userName);
            context.setVariable("expiredTokens", expiredTokens);
            context.setVariable("dateFormatter", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            context.setVariable("recommendation", "Please purchase a new token to ensure uninterrupted electricity supply. Check your meter balance and contact support if needed.");

            String htmlContent = templateEngine.process("token-expiration-notifier", context);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Successfully sent token expiration email to {}", toEmail);
        } catch (MessagingException e) {
            log.error("Failed to send token expiration email to {}: {}", toEmail, e.getMessage(), e);
            throw e;
        }
    }
}