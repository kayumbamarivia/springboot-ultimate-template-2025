package com.spring.fortress.vehicles.dtos.request;

import jakarta.validation.constraints.*;
import java.time.Year;

/**
 * A DTO for creating or updating a vehicle in the vehicle tracking system.
 * Encapsulates vehicle details with strict validation constraints.
 *
 * @param chassisNumber    the vehicle's chassis number (VIN)
 * @param manufacturer     the vehicle's manufacturer
 * @param manufacturedYear the year the vehicle was manufactured
 * @param price            the vehicle's price
 * @param modelName        the vehicle's model name
 * @param ownerId          the ID of the vehicle's owner
 * @author Fortress Backend
 * @since 1.0
 */
public record VehicleRequest(
        @NotBlank(message = "Chassis number is required")
        @Size(min = 17, max = 17, message = "Chassis number must be exactly 17 characters")
        @Pattern(regexp = "^[A-HJ-NPR-Z0-9]{17}$", message = "Chassis number must be alphanumeric (excluding I, O, Q)")
        String chassisNumber,

        @NotBlank(message = "Manufacturer is required")
        @Size(max = 50, message = "Manufacturer must be 50 characters or less")
        String manufacturer,

        @NotNull(message = "Manufactured year is required")
        Integer manufacturedYear,

        @PositiveOrZero(message = "Price must be zero or positive")
        @DecimalMax(value = "10000000.00", message = "Price must not exceed 10 million")
        Double price,

        @NotBlank(message = "Model name is required")
        @Size(max = 50, message = "Model name must be 50 characters or less")
        String modelName,

        @Positive(message = "Owner ID must be positive")
        Long ownerId
) {
}