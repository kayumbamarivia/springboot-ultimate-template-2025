package com.spring.fortress.vehicles.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * A DTO for requesting account activation in the vehicle tracking system.
 * Includes user details and verification code for activation.
 *
 * @param email     the user's email address
 * @param fullName         the user's full name
 * @param verificationCode the verification code for activation
 * @param expiresAt        the expiration time of the verification code
 * @author Fortress Backend
 * @since 1.0
 */
public record AccountActivationRequest(
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        @Size(max = 50, message = "Email must be 50 characters or less")
        String email,

        @NotBlank(message = "Full name is required")
        @Size(max = 30, message = "Full name must be 30 characters or less")
        String fullName,

        @NotBlank(message = "Verification code is required")
        @Size(min = 6, max = 6, message = "Verification code must be 6 characters")
        String verificationCode,

        @NotBlank(message = "Expiration time is required")
        @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$", message = "Expiration time must be in format 'yyyy-MM-dd HH:mm:ss'")
        String expiresAt
) {
}