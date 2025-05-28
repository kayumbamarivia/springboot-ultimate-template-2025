package com.spring.fortress.vehicles.controllers;

import com.spring.fortress.vehicles.dtos.request.PlateRequest;
import com.spring.fortress.vehicles.dtos.response.ErrorResponse;
import com.spring.fortress.vehicles.models.Plate;
import com.spring.fortress.vehicles.services.PlateService;
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
 * Controller for managing vehicle license plate operations.
 * Provides endpoints for registering and retrieving plates.
 *
 * @author Fortress Backend
 * @since 1.0
 */
@Tag(name = "Plates", description = "Endpoints for vehicle license plate management")
@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/plates")
public class PlateController {

    private final PlateService plateService;

    /**
     * Registers a new license plate.
     *
     * @param request the plate registration request
     * @return a response with the registration result
     */
    @Operation(summary = "Register a new plate", description = "Creates a new license plate record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Plate registered successfully",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Unauthorized access")
    })
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody PlateRequest request) {
        log.info("Processing plate registration for plate number: {}", request.plateNumber());
        String result = plateService.registerPlate(request);
        return ResponseEntity.ok(result);
    }

    /**
     * Retrieves all plates for a specific owner.
     *
     * @param ownerId the ID of the owner
     * @return a response with the list of plates
     */
    @Operation(summary = "Get plates by owner", description = "Retrieves all plates for a specific owner")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Plates retrieved successfully",
                    content = @Content(schema = @Schema(implementation = Plate.class))),
            @ApiResponse(responseCode = "400", description = "Invalid owner ID",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Unauthorized access")
    })
    @GetMapping("/all/{ownerId}")
    public ResponseEntity<List<Plate>> getAll(@PathVariable long ownerId) {
        log.info("Fetching plates for owner ID: {}", ownerId);
        // Placeholder: Implement plateRepository.findByOwnerId() in PlateService
        throw new UnsupportedOperationException("Get plates by owner ID not implemented");
    }
}