package com.spring.fortress.vehicles.controllers;

import com.spring.fortress.vehicles.utils.ElectricityTokenUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller for electricity token operations.
 * Provides endpoints for token generation, verification, and simulation.
 */
@RestController
@RequestMapping("/electricity/tokens")
@RequiredArgsConstructor
@Slf4j
public class ElectricityTokenController {

    private final ElectricityTokenUtil electricityTokenUtil;

    /**
     * Generate a new electricity token.
     * POST /electricity/tokens/generate
     */
    @PostMapping("/generate")
    public ResponseEntity<Map<String, Object>> generateToken(@RequestBody TokenGenerationRequest request) {
        try {
            // Validate input
            if (request.getMeterNumber() == null || request.getMeterNumber().trim().isEmpty()) {
                return createErrorResponse("Meter number is required", HttpStatus.BAD_REQUEST);
            }
            if (request.getUnits() <= 0) {
                return createErrorResponse("Units must be greater than 0", HttpStatus.BAD_REQUEST);
            }

            // Generate token
            String token = electricityTokenUtil.generateToken(request.getMeterNumber(), request.getUnits());

            // Calculate TID for response
            long tid = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) -
                    LocalDateTime.of(1993, 1, 1, 0, 0).toEpochSecond(ZoneOffset.UTC);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Token generated successfully");
            response.put("data", Map.of(
                    "token", token,
                    "meterNumber", request.getMeterNumber(),
                    "units", request.getUnits(),
                    "tid", tid,
                    "generatedAt", LocalDateTime.now(),
                    "expiresIn", "30 days"
            ));

            log.info("Generated token for meter {}: {}", request.getMeterNumber(), token);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error generating token: {}", e.getMessage(), e);
            return createErrorResponse("Failed to generate token: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Verify an electricity token.
     * POST /electricity/tokens/verify
     */
    @PostMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyToken(@RequestBody TokenVerificationRequest request) {
        try {
            // Validate input
            if (request.getToken() == null || request.getToken().trim().isEmpty()) {
                return createErrorResponse("Token is required", HttpStatus.BAD_REQUEST);
            }
            if (request.getMeterNumber() == null || request.getMeterNumber().trim().isEmpty()) {
                return createErrorResponse("Meter number is required", HttpStatus.BAD_REQUEST);
            }

            // Verify token
            boolean isValid = electricityTokenUtil.isTokenValid(
                    request.getToken(),
                    request.getMeterNumber(),
                    request.getUnits(),
                    request.getTid()
            );

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", isValid ? "Token is valid" : "Token is invalid");
            response.put("data", Map.of(
                    "token", request.getToken(),
                    "meterNumber", request.getMeterNumber(),
                    "isValid", isValid,
                    "verifiedAt", LocalDateTime.now(),
                    "units", request.getUnits(),
                    "tid", request.getTid()
            ));

            log.info("Token verification for meter {}: {} - {}",
                    request.getMeterNumber(), request.getToken(), isValid ? "VALID" : "INVALID");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error verifying token: {}", e.getMessage(), e);
            return createErrorResponse("Failed to verify token: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Simulate token purchase - generates token and simulates payment.
     * POST /electricity/tokens/purchase
     */
    @PostMapping("/purchase")
    public ResponseEntity<Map<String, Object>> purchaseToken(@RequestBody TokenPurchaseRequest request) {
        try {
            // Validate input
            if (request.getMeterNumber() == null || request.getMeterNumber().trim().isEmpty()) {
                return createErrorResponse("Meter number is required", HttpStatus.BAD_REQUEST);
            }
            if (request.getAmount() <= 0) {
                return createErrorResponse("Amount must be greater than 0", HttpStatus.BAD_REQUEST);
            }

            // Simulate rate calculation (e.g., 1 unit = $0.15)
            double rate = 0.15;
            double units = request.getAmount() / rate;

            // Generate token
            String token = electricityTokenUtil.generateToken(request.getMeterNumber(), units);

            long tid = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) -
                    LocalDateTime.of(1993, 1, 1, 0, 0).toEpochSecond(ZoneOffset.UTC);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Token purchased successfully");
            response.put("data", Map.of(
                    "transactionId", "TXN-" + System.currentTimeMillis(),
                    "token", token,
                    "meterNumber", request.getMeterNumber(),
                    "amountPaid", request.getAmount(),
                    "unitsAllocated", Math.round(units * 100.0) / 100.0,
                    "rate", rate,
                    "tid", tid,
                    "purchaseDate", LocalDateTime.now(),
                    "expiryDate", LocalDateTime.now().plusDays(30)
            ));

            log.info("Token purchased for meter {}: {} - ${} for {} units",
                    request.getMeterNumber(), token, request.getAmount(), units);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error processing token purchase: {}", e.getMessage(), e);
            return createErrorResponse("Failed to process purchase: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get meter information and current balance simulation.
     * GET /electricity/tokens/meter/{meterNumber}
     */
    @GetMapping("/meter/{meterNumber}")
    public ResponseEntity<Map<String, Object>> getMeterInfo(@PathVariable String meterNumber) {
        try {
            // Simulate meter data
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Meter information retrieved successfully");
            response.put("data", Map.of(
                    "meterNumber", meterNumber,
                    "customerName", "John Doe",
                    "currentBalance", Math.round(Math.random() * 100 * 100.0) / 100.0,
                    "lastTopUp", LocalDateTime.now().minusDays((long)(Math.random() * 30)),
                    "averageDailyUsage", Math.round(Math.random() * 10 * 100.0) / 100.0,
                    "meterStatus", "ACTIVE",
                    "lastReading", LocalDateTime.now().minusHours(1),
                    "totalTokensUsed", (long)(Math.random() * 50) + 10
            ));

            log.info("Retrieved meter info for: {}", meterNumber);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error fetching meter info: {}", e.getMessage(), e);
            return createErrorResponse("Failed to fetch meter info: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Simulate token usage/redemption.
     * POST /electricity/tokens/redeem
     */
    @PostMapping("/redeem")
    public ResponseEntity<Map<String, Object>> redeemToken(@RequestBody TokenRedemptionRequest request) {
        try {
            // Validate input
            if (request.getToken() == null || request.getToken().trim().isEmpty()) {
                return createErrorResponse("Token is required", HttpStatus.BAD_REQUEST);
            }
            if (request.getMeterNumber() == null || request.getMeterNumber().trim().isEmpty()) {
                return createErrorResponse("Meter number is required", HttpStatus.BAD_REQUEST);
            }

            // Simulate token redemption
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Token redeemed successfully");
            response.put("data", Map.of(
                    "token", request.getToken(),
                    "meterNumber", request.getMeterNumber(),
                    "unitsAdded", Math.round(Math.random() * 100 * 100.0) / 100.0,
                    "newBalance", Math.round(Math.random() * 200 * 100.0) / 100.0,
                    "redemptionDate", LocalDateTime.now(),
                    "redemptionId", "RED-" + System.currentTimeMillis()
            ));

            log.info("Token redeemed for meter {}: {}", request.getMeterNumber(), request.getToken());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error redeeming token: {}", e.getMessage(), e);
            return createErrorResponse("Failed to redeem token: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get token history for a meter.
     * GET /electricity/tokens/history/{meterNumber}
     */
    @GetMapping("/history/{meterNumber}")
    public ResponseEntity<Map<String, Object>> getTokenHistory(@PathVariable String meterNumber,
                                                               @RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "10") int size) {
        try {
            // Simulate token history
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Token history retrieved successfully");
            response.put("data", Map.of(
                    "meterNumber", meterNumber,
                    "totalTokens", 25,
                    "page", page,
                    "size", size,
                    "tokens", java.util.List.of(
                            Map.of("token", "12345-67890-12345-67890", "units", 50.0, "date", LocalDateTime.now().minusDays(1), "status", "USED"),
                            Map.of("token", "98765-43210-98765-43210", "units", 75.0, "date", LocalDateTime.now().minusDays(5), "status", "USED"),
                            Map.of("token", "11111-22222-33333-44444", "units", 100.0, "date", LocalDateTime.now().minusDays(10), "status", "EXPIRED")
                    )
            ));

            log.info("Retrieved token history for meter: {}", meterNumber);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error fetching token history: {}", e.getMessage(), e);
            return createErrorResponse("Failed to fetch token history: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Health check endpoint.
     * GET /electricity/tokens/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Electricity Token Service is running");
        response.put("timestamp", LocalDateTime.now());
        response.put("version", "1.0.0");
        return ResponseEntity.ok(response);
    }

    /**
     * Helper method to create error responses.
     */
    private ResponseEntity<Map<String, Object>> createErrorResponse(String message, HttpStatus status) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("message", message);
        errorResponse.put("timestamp", LocalDateTime.now());
        return ResponseEntity.status(status).body(errorResponse);
    }

    // Request DTOs
    @Setter
    @Getter
    public static class TokenGenerationRequest {
        // Getters and setters
        private String meterNumber;
        private double units;

    }

    @Setter
    @Getter
    public static class TokenVerificationRequest {
        // Getters and setters
        private String token;
        private String meterNumber;
        private double units;
        private long tid;

    }

    @Setter
    @Getter
    public static class TokenPurchaseRequest {
        // Getters and setters
        private String meterNumber;
        private double amount;

    }

    @Setter
    @Getter
    public static class TokenRedemptionRequest {
        // Getters and setters
        private String token;
        private String meterNumber;

    }
}
