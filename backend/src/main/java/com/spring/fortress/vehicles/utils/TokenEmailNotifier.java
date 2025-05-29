package com.spring.fortress.vehicles.utils;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenEmailNotifier {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    private static final String TARGET_EMAIL = "kayumbaj88@gmail.com";
    private static final String TARGET_USER_NAME = "Kayumba";

    @Scheduled(fixedRate = 600000) // 10 minutes
    public void sendScheduledTokenExpirationEmail() {
        try {
            List<String> sampleExpiredTokens = generateSampleExpiredTokens();
            log.info("Sending scheduled token expiration email to {}", TARGET_EMAIL);
            sendTokenExpirationEmail(TARGET_EMAIL, TARGET_USER_NAME, sampleExpiredTokens);
        } catch (Exception e) {
            log.error("Failed to send scheduled token expiration email: {}", e.getMessage(), e);
        }
    }

    public void sendTokenExpirationEmail(String toEmail, String userName, List<String> expiredTokens)
            throws MessagingException {
        if (toEmail == null || toEmail.trim().isEmpty()) {
            throw new IllegalArgumentException("Recipient email cannot be null or empty");
        }
        if (userName == null || userName.trim().isEmpty()) {
            throw new IllegalArgumentException("User name cannot be null or empty");
        }

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromEmail);
        helper.setTo(toEmail);
        helper.setSubject("Electricity Token Expiration Notice - " +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

        Context context = new Context();
        context.setVariable("userName", userName);
        context.setVariable("expiredTokens", expiredTokens);
        context.setVariable("currentDateTime", LocalDateTime.now());
        context.setVariable("dateFormatter", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        context.setVariable("recommendation",
                "Please purchase a new token to ensure uninterrupted electricity supply.");

        String htmlContent = templateEngine.process("token-expiration-notifier", context);
        helper.setText(htmlContent, true);

        mailSender.send(message);
        log.info("Successfully sent token expiration email to {} at {}", toEmail, LocalDateTime.now());
    }

    private List<String> generateSampleExpiredTokens() {
        Random random = new Random();
        return Arrays.asList(
                "TOKEN-" + (1000 + random.nextInt(9000)),
                "TOKEN-" + (1000 + random.nextInt(9000)),
                "TOKEN-" + (1000 + random.nextInt(9000))
        );
    }
}