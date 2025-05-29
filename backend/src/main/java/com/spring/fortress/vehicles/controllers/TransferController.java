package com.spring.fortress.vehicles.controllers;

import com.spring.fortress.vehicles.dtos.request.TransferRequest;
import com.spring.fortress.vehicles.dtos.response.ErrorResponse;
import com.spring.fortress.vehicles.models.Transfer;
import com.spring.fortress.vehicles.services.TransferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for managing vehicle ownership transfer operations.
 * Provides endpoints for initiating transfers and retrieving transfer history.
 *
 * @author Fortress Backend
 * @since 1.0
 */
@Tag(name = "Transfers", description = "Endpoints for vehicle ownership transfers")
@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/transfers")
public class TransferController {

    private final TransferService transferService;

    /**
     * Initiates a new vehicle ownership transfer.
     *
     * @param request the transfer request
     * @return a response with the transfer result
     */
    @Operation(summary = "Initiate a vehicle ownership transfer", description = "Registers a new transfer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transfer registered successfully",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Unauthorized access")
    })
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody TransferRequest request) {
        log.info("Processing ownership transfer for vehicle ID: {}", request.vehicleId());
        String result = transferService.transferOwnerShip(request);
        return ResponseEntity.ok(result);
    }

    /**
     * Retrieves all transfers.
     *
     * @return a response with the list of transfers
     */
    @Operation(summary = "Get all transfers", description = "Retrieves all transfer records")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transfers retrieved successfully",
                    content = @Content(schema = @Schema(implementation = Transfer.class))),
            @ApiResponse(responseCode = "403", description = "Unauthorized access",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/all")
    public ResponseEntity<List<Transfer>> getAll() {
        log.info("Fetching all transfers");
        List<Transfer> transfers = transferService.getAll();
        return ResponseEntity.ok(transfers);
    }

    /**
     * Retrieves the transfer history for a vehicle or owner.
     *
     * @return a response with the transfer history
     */
    @Operation(summary = "Get transfer history", description = "Retrieves transfer history for a vehicle or owner")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "History retrieved successfully",
                    content = @Content(schema = @Schema(implementation = Transfer.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Unauthorized access")
    })
    @GetMapping("/history/{ownerId}/{vehicleId}")
    public ResponseEntity<List<Transfer>> getHistory(@PathVariable Long ownerId, @PathVariable Long vehicleId) {
        log.info("Fetching transfer history");
        List<Transfer> transfers = transferService.getHistory(vehicleId, ownerId);
        return ResponseEntity.ok(transfers);
    }
}