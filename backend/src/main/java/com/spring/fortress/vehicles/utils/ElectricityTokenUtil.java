package com.spring.fortress.vehicles.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Utility class for generating and verifying compliant electricity tokens.
 */
@Component
@Slf4j
public class ElectricityTokenUtil {

    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final String VENDING_KEY = "REG_SECRET_KEY";

    /**
     * Generates a 20-digit electricity token in the format XXXXX-XXXXX-XXXXX-XXXXX.
     *
     * @param meterNumber The meter number
     * @param units       The electricity units (kWh)
     * @return The formatted token
     */
    public String generateToken(String meterNumber, double units) {
        try {
            // Generate TID
            long tid = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) -
                    LocalDateTime.of(1993, 1, 1, 0, 0).toEpochSecond(ZoneOffset.UTC);

            // Create token data
            String tokenData = String.format("%s|%d|%.2f", meterNumber, tid, units);

            // Generate HMAC-SHA256 signature
            String signature = createHmacSignature(tokenData, VENDING_KEY);

            // Convert signature to 20-digit numeric token
            String numericToken = signatureToNumericToken(signature);

            // Format as XXXXX-XXXXX-XXXXX-XXXXX
            String formattedToken = formatToken(numericToken);

            log.info("Generated electricity token for meter {}: {}", meterNumber, formattedToken);
            return formattedToken;

        } catch (Exception e) {
            log.error("Failed to generate electricity token: {}", e.getMessage());
            throw new RuntimeException("Error generating electricity token", e);
        }
    }

    /**
     * Verifies an electricity token.
     *
     * @param token       The token (e.g., "12345-67890-12345-67890")
     * @param meterNumber The meter number (DRN)
     * @param units       The electricity units (kWh)
     * @param tid         The Token Identifier (TID)
     * @return True if valid, false otherwise
     */
    public boolean isTokenValid(String token, String meterNumber, double units, long tid) {
        try {
            // Remove hyphens for verification
            String numericToken = token.replace("-", "");

            // Recreate token data
            String tokenData = String.format("%s|%d|%.2f", meterNumber, tid, units);

            // Generate expected signature
            String expectedSignature = createHmacSignature(tokenData, VENDING_KEY);

            // Convert to expected numeric token
            String expectedNumericToken = signatureToNumericToken(expectedSignature);

            // Compare
            boolean isValid = numericToken.equals(expectedNumericToken);
            if (isValid) {
                log.debug("Electricity token is valid for meter {}", meterNumber);
            } else {
                log.warn("Invalid electricity token for meter {}", meterNumber);
            }
            return isValid;

        } catch (Exception e) {
            log.error("Error verifying electricity token: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Creates an HMAC-SHA256 signature for the given data.
     */
    private String createHmacSignature(String data, String key)
            throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance(HMAC_ALGORITHM);
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM);
        mac.init(secretKeySpec);
        byte[] signatureBytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(signatureBytes);
    }

    /**
     * Converts an HMAC signature to a 20-digit numeric token.
     */
    private String signatureToNumericToken(String signature) {
        // Take first 20 bytes of signature and map to digits
        StringBuilder numeric = new StringBuilder();
        for (int i = 0; i < 20; i++) {
            int byteValue = (i < signature.length()) ? (signature.charAt(i) & 0xFF) % 10 : 0;
            numeric.append(byteValue);
        }
        return numeric.toString();
    }

    /**
     * Formats a 20-digit numeric token as XXXXX-XXXXX-XXXXX-XXXXX.
     */
    private String formatToken(String numericToken) {
        if (numericToken.length() != 20) {
            throw new IllegalArgumentException("Numeric token must be 20 digits");
        }
        return String.format("%s-%s-%s-%s",
                numericToken.substring(0, 5),
                numericToken.substring(5, 10),
                numericToken.substring(10, 15),
                numericToken.substring(15, 20));
    }

    /**
     * Converts bytes to a hex string.
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder hex = new StringBuilder();
        for (byte b : bytes) {
            hex.append(String.format("%02x", b));
        }
        return hex.toString();
    }

    /**
     * Main method for manual testing of token generation and verification.
     */
    public static void main(String[] args) {
        ElectricityTokenUtil tokenUtil = new ElectricityTokenUtil();

        // Test data
        String meterNumber = "12345678901";
        double units = 100.0;
        long tid = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) -
                LocalDateTime.of(1993, 1, 1, 0, 0).toEpochSecond(ZoneOffset.UTC);

        // Test token generation
        String token = tokenUtil.generateToken(meterNumber, units);
        System.out.println("Generated Token: " + token);

        // Test token verification
        boolean isValid = tokenUtil.isTokenValid(token, meterNumber, units, tid);
        System.out.println("Is Token Valid? " + isValid);

        // Test invalid token
        String invalidToken = "00000-00000-00000-00000";
        boolean isInvalidValid = tokenUtil.isTokenValid(invalidToken, meterNumber, units, tid);
        System.out.println("Is Invalid Token Valid? " + isInvalidValid);
    }
}