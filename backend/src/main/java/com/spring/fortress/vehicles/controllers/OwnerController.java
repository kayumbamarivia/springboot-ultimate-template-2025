package com.spring.fortress.vehicles.controllers;

import com.spring.fortress.vehicles.dtos.request.OwnerRequest;
import com.spring.fortress.vehicles.dtos.response.ErrorResponse;
import com.spring.fortress.vehicles.models.Owner;
import com.spring.fortress.vehicles.models.User;
import com.spring.fortress.vehicles.services.OwnerService;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for managing owner-related operations.
 * Provides endpoints for registering, retrieving, and searching owners.
 *
 * @author Fortress Backend
 * @since 1.0
 */
@Tag(name = "Owners", description = "Endpoints for owner management")
@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/owners")
public class OwnerController {

    private final OwnerService ownerService;

    /**
     * Registers a new owner.
     *
     * @param request the owner registration request
     * @return a response with the registration result
     */
    @Operation(summary = "Register a new owner", description = "Creates a new owner record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Owner registered successfully",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Unauthorized access")
    })
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody OwnerRequest request) {
        log.info("Processing owner registration for user ID: {}", request.userId());
        String result = ownerService.registerOwner(request);
        return ResponseEntity.ok(result);
    }

    /**
     * Retrieves all owners
     *
     * @return a response with the list of owners
     */
    @Operation(summary = "Get all owners", description = "Retrieves all owner records (admin-only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Owners retrieved successfully",
                    content = @Content(schema = @Schema(implementation = Owner.class))),
            @ApiResponse(responseCode = "403", description = "Unauthorized access",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/all")
    public ResponseEntity<List<User>> getAll() {
        log.info("Fetching all owners for admin");
        List<User> result = ownerService.getAll();
        return ResponseEntity.ok(result);
    }

    /**
     * Searches for owners by a search term (e.g., email, name).
     *
     * @param searchTerm the term to search for
     * @return a response with the search results
     */
    @Operation(summary = "Search owners", description = "Searches owners by email or name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Owners retrieved successfully",
                    content = @Content(schema = @Schema(implementation = Owner.class))),
            @ApiResponse(responseCode = "400", description = "Invalid search term",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Unauthorized access")
    })
    @GetMapping("/search/{searchTerm}")
    public ResponseEntity<List<User>> search(@PathVariable String searchTerm) {
        log.info("Searching owners with term: {}", searchTerm);
        List<User> result = ownerService.search(searchTerm);
        return ResponseEntity.ok(result);
    }

}