package com.spring.fortress.vehicles.dtos.request;

import com.spring.fortress.vehicles.enums.PlateStatus;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * A DTO for creating or updating a vehicle license plate in the vehicle tracking system.
 * Includes plate details with strict validation, including format checks.
 *
 * @param plateNumber the license plate number
 * @param status      the status of the plate
 * @param issuedDate  the date the plate was issued
 * @param ownerId     the ID of the owner
 * @param vehicleId   the ID of the vehicle
 * @author Fortress Backend
 * @since 1.0
 */
public record PlateRequest(
        @NotBlank(message = "Plate number is required")
        @Size(max = 20, message = "Plate number must be 20 characters or less")
//        @Pattern(regexp = "^R[A-Z]{2}\\s\\d{3}\\s[A-Z]$", message = "Plate number must follow format 'RXX 123 X' (e.g., 'RAD 123 B')")
        String plateNumber,

        @NotNull(message = "Plate status is required")
        PlateStatus status,

        @NotNull(message = "Issued date is required")
        LocalDateTime issuedDate,

        @Positive(message = "Owner ID must be positive")
        Long ownerId,

        @Positive(message = "Vehicle ID must be positive")
        Long vehicleId
) {
        /**
         * Validates the plate number format programmatically.
         *
         * @return true if the plate number matches the required format, false otherwise
         */
//        public boolean isValidFormat() {
//                return plateNumber != null && plateNumber.matches("^R[A-Z]{2}\\s\\d{3}\\s[A-Z]$");
//        }
}