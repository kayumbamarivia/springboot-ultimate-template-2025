package com.spring.fortress.vehicles.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * A DTO for requesting a password change in the vehicle tracking system.
 * Includes the user's email, verification code, old password, and new password.
 *
 * @param email the user's email address
 * @param code         the verification code
 * @param oldPassword  the current password
 * @param newPassword  the new password
 * @author Fortress Backend
 * @since 1.0
 */
public record NewPassWordRequest(
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        @Size(max = 50, message = "Email must be 50 characters or less")
        String email,

        @NotBlank(message = "Verification code is required")
        @Size(min = 6, max = 6, message = "Verification code must be 6 characters")
        String code,

        @NotBlank(message = "Old password is required")
        @Size(min = 8, max = 50, message = "Old password must be between 8 and 50 characters")
        String oldPassword,

        @NotBlank(message = "New password is required")
        @Size(min = 8, max = 50, message = "New password must be between 8 and 50 characters")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$", message = "New password must contain at least one letter and one number")
        String newPassword
) {
}