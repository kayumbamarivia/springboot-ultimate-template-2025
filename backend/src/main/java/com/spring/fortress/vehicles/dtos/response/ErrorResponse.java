package com.spring.fortress.vehicles.dtos.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A DTO for responding with error details in the vehicle tracking system.
 * Provides standardized error information for API clients.
 *
 * @param status    the HTTP status code
 * @param message   the error message
 * @param timestamp the time the error occurred
 * @author Fortress Backend
 * @since 1.0
 */
public record ErrorResponse(
        @PositiveOrZero(message = "Status code must be zero or positive")
        int status,

        @NotBlank(message = "Error message is required")
        String message,

        @NotBlank(message = "Timestamp is required")
        String timestamp
) {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Creates an ErrorResponse with the current timestamp.
     *
     * @param status  the HTTP status code
     * @param message the error message
     * @return a new ErrorResponse instance
     */
    public static ErrorResponse of(int status, String message) {
        return new ErrorResponse(status, message, LocalDateTime.now().format(FORMATTER));
    }
}