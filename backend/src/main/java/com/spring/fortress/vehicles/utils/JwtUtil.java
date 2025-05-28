package com.spring.fortress.vehicles.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility for creating and validating JSON Web Tokens (JWT) using HMAC-SHA256.
 * Supports token creation, validation, and claim extraction for secure authentication.
 *
 * @author Fortress Backend
 * @since 1.0
 */
@Component
@Slf4j
public class JwtUtil {

    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final String DOT_SEPARATOR = ".";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Creates a JWT token with HMAC-SHA256 signature.
     *
     * @param secretKey       the secret key for signing
     * @param subject         the subject (e.g., user ID or username)
     * @param issuer          the issuer of the token
     * @param expiryInSeconds the token expiry duration in seconds
     * @return the generated JWT token
     * @throws RuntimeException if token creation fails
     */
    public static String createToken(String secretKey, String subject, String issuer, long expiryInSeconds) {
        try {
            Map<String, String> header = new HashMap<>();
            header.put("alg", "HS256");
            header.put("typ", "JWT");
            String encodedHeader = encodeBase64UrlSafe(objectMapper.writeValueAsString(header));

            Map<String, Object> payload = new HashMap<>();
            payload.put("sub", subject);
            payload.put("iss", issuer);
            payload.put("iat", System.currentTimeMillis() / 1000);
            payload.put("exp", (System.currentTimeMillis() / 1000) + expiryInSeconds);
            String encodedPayload = encodeBase64UrlSafe(objectMapper.writeValueAsString(payload));

            String dataToSign = encodedHeader + DOT_SEPARATOR + encodedPayload;
            String signature = createHmacSignature(dataToSign, secretKey);
            String encodedSignature = encodeBase64UrlSafe(signature);

            String token = encodedHeader + DOT_SEPARATOR + encodedPayload + DOT_SEPARATOR + encodedSignature;
            log.info("Successfully created JWT token for subject: {}", subject);
            return token;

        } catch (JsonProcessingException e) {
            log.error("Failed to serialize JWT header or payload: {}", e.getMessage(), e);
            throw new RuntimeException("Error creating JWT token: serialization failed", e);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("HMAC operation failed: {}", e.getMessage(), e);
            throw new RuntimeException("Error creating JWT token: HMAC operation failed", e);
        }
    }

    /**
     * Validates a JWT token by checking its signature and expiration.
     *
     * @param token     the JWT token to validate
     * @param secretKey the secret key used for signing
     * @return {@code true} if the token is valid and not expired, {@code false} otherwise
     */
    public static boolean isTokenValid(String token, String secretKey) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                log.warn("Invalid JWT token format: incorrect number of parts");
                return false;
            }

            String dataToSign = parts[0] + DOT_SEPARATOR + parts[1];
            String expectedSignature = createHmacSignature(dataToSign, secretKey);
            String encodedExpectedSignature = encodeBase64UrlSafe(expectedSignature);
            if (!encodedExpectedSignature.equals(parts[2])) {
                log.warn("Invalid JWT token: signature mismatch");
                return false;
            }

            if (isTokenExpired(token)) {
                log.warn("JWT token is expired");
                return false;
            }

            log.debug("JWT token is valid");
            return true;

        } catch (Exception e) {
            log.error("Error validating JWT token: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Checks if a JWT token has expired.
     *
     * @param token the JWT token
     * @return {@code true} if expired, {@code false} otherwise
     */
    public static boolean isTokenExpired(String token) {
        try {
            Long exp = extractClaim(token, "exp", Long.class);
            if (exp == null) {
                log.warn("JWT token has no expiration claim");
                return true;
            }
            return exp <= System.currentTimeMillis() / 1000;
        } catch (Exception e) {
            log.error("Error checking token expiration: {}", e.getMessage(), e);
            return true;
        }
    }

    /**
     * Extracts the username (subject) from a JWT token.
     *
     * @param token the JWT token
     * @return the subject (username or user ID) or null if extraction fails
     */
    public static String extractUsername(String token) {
        try {
            return extractClaim(token, "sub", String.class);
        } catch (Exception e) {
            log.error("Error extracting username from JWT token: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Extracts a specific claim from the JWT payload.
     *
     * @param token    the JWT token
     * @param claim    the claim name (e.g., "sub", "iss", "exp")
     * @param type     the expected type of the claim
     * @param <T>      the type parameter
     * @return the claim value or null if extraction fails
     * @throws JsonProcessingException if JSON parsing fails
     */
    @SuppressWarnings("unchecked")
    private static <T> T extractClaim(String token, String claim, Class<T> type) throws JsonProcessingException {
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            log.warn("Invalid JWT token format for claim extraction");
            return null;
        }

        String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
        Map<String, Object> payload = objectMapper.readValue(payloadJson, Map.class);
        Object value = payload.get(claim);

        if (value == null) {
            log.warn("Claim {} not found in JWT token", claim);
            return null;
        }

        // Handle numeric type conversions (e.g., Integer -> Long)
        if (type == Long.class && value instanceof Integer) {
            return type.cast(((Integer) value).longValue());
        }

        if (type.isInstance(value)) {
            return type.cast(value);
        }

        log.warn("Claim {} has invalid type in JWT token (expected: {}, actual: {})",
                claim, type.getSimpleName(), value.getClass().getSimpleName());
        return null;
    }

    /**
     * Creates an HMAC-SHA256 signature for the given data.
     *
     * @param data      the data to sign (header.payload)
     * @param secretKey the secret key
     * @return the HMAC signature
     * @throws NoSuchAlgorithmException if the HMAC algorithm is not available
     * @throws InvalidKeyException      if the secret key is invalid
     */
    private static String createHmacSignature(String data, String secretKey)
            throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance(HMAC_ALGORITHM);
        SecretKeySpec secretKeySpec = new SecretKeySpec(
                secretKey.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM);
        mac.init(secretKeySpec);
        byte[] signatureBytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(signatureBytes);
    }

    /**
     * Encodes a string to Base64 URL-safe format.
     *
     * @param input the input string to encode
     * @return the Base64 URL-safe encoded string
     */
    private static String encodeBase64UrlSafe(String input) {
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(input.getBytes(StandardCharsets.UTF_8));
    }
}