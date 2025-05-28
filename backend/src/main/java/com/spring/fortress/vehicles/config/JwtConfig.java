package com.spring.fortress.vehicles.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * Configuration class for JWT authentication properties.
 * <p>
 * This class centralizes the configuration properties needed for JWT token
 * generation and validation in the application. Values are injected from
 * application properties/yml files.
 * </p>
 *
 * @author Fortress Backend
 * @version 1.0
 * @since 1.0
 */
@Component
@Configuration
@Data
public class JwtConfig {
    /**
     * Secret key used for signing JWT tokens.
     * Should be kept secure and not exposed in client-side code.
     */
    @Value("${jwt.secret}")
    private String secretKey;

    /**
     * The issuer claim for the JWT token.
     * Identifies the principal that issued the token.
     */
    @Value("${jwt.issuer}")
    private String issuer;

    /**
     * Token validity period in seconds.
     * Determines how long the JWT token is valid after issuance.
     */
    @Value("${jwt.expiry}")
    private Long expiryInSeconds;

    /**
     * Refresh token validity period in seconds.
     * Typically longer than the regular token expiry.
     */
    @Value("${jwt.refresh-expiry:604800}") // Default 7 days
    private Long refreshExpiryInSeconds;
}