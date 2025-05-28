package com.spring.fortress.vehicles.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * Utility class for generating verification codes and handling date-time operations.
 * Provides methods for creating secure verification codes and managing expiration times.
 *
 * @author Fortress Backend
 * @since 1.0
 */
public class VerificationUtil {

    private static final int CODE_LENGTH = 6;
    private static final int MIN_CODE = 100_000;
    private static final int MAX_CODE_RANGE = 900_000;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Generates a random 6-digit verification code.
     *
     * @return a 6-digit string representation of the verification code
     */
    public static String generateVerificationCode() {
        Random random = new Random();
        int code = MIN_CODE + random.nextInt(MAX_CODE_RANGE);
        return String.format("%0" + CODE_LENGTH + "d", code);
    }

    /**
     * Calculates the expiration time by adding the specified minutes to the current time.
     *
     * @param minutes the number of minutes until expiration
     * @return the expiration time as a {@link LocalDateTime} object
     * @throws IllegalArgumentException if minutes is negative
     */
    public static LocalDateTime getExpirationTime(int minutes) {
        if (minutes < 0) {
            throw new IllegalArgumentException("Expiration time cannot be negative");
        }
        return LocalDateTime.now().plusMinutes(minutes);
    }

    /**
     * Formats a {@link LocalDateTime} object into a string with the pattern "yyyy-MM-dd HH:mm:ss".
     *
     * @param dateTime the {@link LocalDateTime} to format
     * @return the formatted date-time string
     * @throws IllegalArgumentException if dateTime is null
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            throw new IllegalArgumentException("DateTime cannot be null");
        }
        return dateTime.format(FORMATTER);
    }

    /**
     * Checks if a verification code has expired based on its expiration time.
     *
     * @param expirationTime the expiration time to check
     * @return {@code true} if the code is expired or expirationTime is null, {@code false} otherwise
     */
    public static boolean isCodeExpired(LocalDateTime expirationTime) {
        return expirationTime == null || LocalDateTime.now().isAfter(expirationTime);
    }
}