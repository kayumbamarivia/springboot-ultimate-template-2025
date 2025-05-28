package com.spring.fortress.vehicles.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * A DTO for user login requests in the vehicle tracking system.
 * Captures email and password for authentication.
 *
 * @param email the user's email address
 * @param password     the user's password
 * @author Fortress Backend
 * @since 1.0
 */
public record LoginRequest(
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        @Size(max = 50, message = "Email must be 50 characters or less")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 50, message = "Password must be between 8 and 50 characters")
        String password
) {
}