package com.spring.fortress.vehicles.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * A DTO for requesting a password reset in the vehicle tracking system.
 * Includes the user's email, full name, and reset code.
 *
 * @param email the user's email address
 * @param fullName     the user's full name
 * @param resetCode    the password reset code
 * @author Fortress Backend
 * @since 1.0
 */
public record ResetPasswordRequest(
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        @Size(max = 50, message = "Email must be 50 characters or less")
        String email,

        @NotBlank(message = "Full name is required")
        @Size(max = 15, message = "Full name must be 15 characters or less")
        String fullName,

        @NotBlank(message = "Reset code is required")
        @Size(min = 6, max = 6, message = "Reset code must be 6 characters")
        String resetCode
) {
}