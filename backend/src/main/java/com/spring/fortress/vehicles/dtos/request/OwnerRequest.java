package com.spring.fortress.vehicles.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * A DTO for creating or updating an owner in the vehicle tracking system.
 * Links a user to ownership via their user ID and email.
 *
 * @param userId       the ID of the user
 * @param email the email address of the owner
 * @author Fortress Backend
 * @since 1.0
 */
public record OwnerRequest(
        @Positive(message = "User ID must be positive")
        Long userId,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        @Size(max = 50, message = "Email must be 50 characters or less")
        String email
) {
}