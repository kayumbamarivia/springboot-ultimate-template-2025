package com.spring.fortress.vehicles.dtos.response;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * A DTO for responding to verification requests in the vehicle tracking system.
 * Returns the verified user's email and full name.
 *
 * @param email the verified email address
 * @param fullName     the verified full name
 * @author Fortress Backend
 * @since 1.0
 */
public record VerificationResponse(
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        @Size(max = 50, message = "Email must be 50 characters or less")
        String email,

        @NotBlank(message = "Full name is required")
        @Size(max = 30, message = "Full name must be 30 characters or less")
        String fullName
) {
}