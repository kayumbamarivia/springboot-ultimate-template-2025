package com.spring.fortress.vehicles.dtos.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

/**
 * A DTO for requesting a vehicle ownership transfer in the vehicle tracking system.
 * Captures details of the transfer, including involved parties and amount.
 * @param issuedDate the date the transfer was issued
 * @param oldOwnerId the ID of the previous owner
 * @param newOwnerId the ID of the new owner
 * @param amount     the transfer amount
 * @param vehicleId  the ID of the vehicle
 * @author Fortress Backend
 * @since 1.0
 */
public record TransferRequest(
        @NotNull(message = "Issued date is required")
        LocalDateTime issuedDate,

        @Positive(message = "Old owner ID must be positive")
        Long oldOwnerId,

        @Positive(message = "New owner ID must be positive")
        Long newOwnerId,

        @PositiveOrZero(message = "Amount must be zero or positive")
        @DecimalMax(value = "10000000.00", message = "Amount must not exceed 10 million")
        Double amount,

        @Positive(message = "Vehicle ID must be positive")
        Long vehicleId
) {
}