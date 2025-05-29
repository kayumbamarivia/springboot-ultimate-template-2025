package com.spring.fortress.vehicles.controllers;

import com.spring.fortress.vehicles.dtos.request.VehicleRequest;
import com.spring.fortress.vehicles.dtos.response.ErrorResponse;
import com.spring.fortress.vehicles.models.Vehicle;
import com.spring.fortress.vehicles.services.VehicleService;
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
 * Controller for managing vehicle-related operations in the vehicle tracking system.
 * Provides endpoints for registering, retrieving, and searching vehicles.
 *
 * @author Fortress Backend
 * @since 1.0
 */
@Tag(name = "Vehicles", description = "Endpoints for vehicle management")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    /**
     * Registers a new vehicle with the provided details.
     *
     * @param request the vehicle registration request
     * @return a response with the registration result
     */
    @Operation(summary = "Register a new vehicle", description = "Creates a new vehicle record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vehicle registered successfully",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Unauthorized access")
    })
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody VehicleRequest request) {
        log.info("Processing vehicle registration for chassis number: {}", request.chassisNumber());
        String result = vehicleService.registerVehicle(request);
        return ResponseEntity.ok(result);
    }

    /**
     * Retrieves all vehicles.
     *
     * @return a response with the list of vehicles
     */
    @Operation(summary = "Get all vehicles", description = "Retrieves all vehicles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vehicles retrieved successfully",
                    content = @Content(schema = @Schema(implementation = Vehicle.class))),
            @ApiResponse(responseCode = "403", description = "Unauthorized access",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/all")
    public ResponseEntity<List<Vehicle>> getAll() {
        log.info("Fetching all vehicles");
        List<Vehicle> vehicles = vehicleService.getAll();
        return ResponseEntity.ok(vehicles);
    }

    /**
     * Searches for vehicles by a search term (e.g., chassis number, model).
     *
     * @param searchTerm the term to search for
     * @return a response with the search results
     */
    @Operation(summary = "Search vehicles", description = "Searches vehicles by chassis number or model")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vehicles retrieved successfully",
                    content = @Content(schema = @Schema(implementation = Vehicle.class))),
            @ApiResponse(responseCode = "400", description = "Invalid search term",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Unauthorized access")
    })
    @GetMapping("/search/{searchTerm}")
    public ResponseEntity<List<Vehicle>> search(@PathVariable String searchTerm) {
        log.info("Searching vehicles with term: {}", searchTerm);
        List<Vehicle> vehicles = vehicleService.search(searchTerm);
        return ResponseEntity.ok(vehicles);
    }

    /**
     * Retrieves vehicles by owner ID.
     *
     * @param ownerId the ID of the owner
     * @return a response with the owner's vehicles
     */
    @Operation(summary = "Get vehicles by owner", description = "Retrieves vehicles for a specific owner")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vehicles retrieved successfully",
                    content = @Content(schema = @Schema(implementation = Vehicle.class))),
            @ApiResponse(responseCode = "400", description = "Invalid owner ID",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Unauthorized access")
    })
    @GetMapping("/get/{ownerId}")
    public ResponseEntity<List<Vehicle>> getByOwnerId(@PathVariable long ownerId) {
        log.info("Fetching vehicles for owner ID: {}", ownerId);
        List<Vehicle> vehicles = vehicleService.getByOwnerId(ownerId);
        return ResponseEntity.ok(vehicles);
    }
}