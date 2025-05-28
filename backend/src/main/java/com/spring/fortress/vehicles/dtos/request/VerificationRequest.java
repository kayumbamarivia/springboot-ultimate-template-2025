package com.spring.fortress.vehicles.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

/**
 * A DTO for requesting verification of a user's email with a verification code.
 * Used in the account activation or verification process.
 *
 * @param verificationCode the verification code sent to the user
 * @param email     the user's email address
 * @author Fortress Backend
 * @since 1.0
 */
public record VerificationRequest(
        @NotBlank(message = "Verification code is required")
        @Size(min = 6, max = 6, message = "Verification code must be 6 characters")
        String verificationCode,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        @Size(max = 50, message = "Email must be less than 50 characters")
        String email
) {
}