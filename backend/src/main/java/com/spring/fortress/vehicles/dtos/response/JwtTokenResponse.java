package com.spring.fortress.vehicles.dtos.response;

import jakarta.validation.constraints.NotBlank;

/**
 * A DTO for responding with a JWT token in the vehicle tracking system.
 * Encapsulates the token issued after successful authentication.
 *
 * @param token the JWT token
 * @author Fortress Backend
 * @since 1.0
 */
public record JwtTokenResponse(
        @NotBlank(message = "JWT token is required")
        String token
) {
}