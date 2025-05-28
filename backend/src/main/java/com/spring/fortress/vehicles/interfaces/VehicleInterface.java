package com.spring.fortress.vehicles.interfaces;

import com.spring.fortress.vehicles.dtos.request.VehicleRequest;
import com.spring.fortress.vehicles.models.Vehicle;
import jakarta.validation.Valid;

import java.util.List;

/**
 * Interface defining vehicle-related operations in the vehicle tracking system.
 *
 * @author Fortress Backend
 * @since 1.0
 */
public interface VehicleInterface {

    /**
     * Registers a new vehicle with the provided details.
     *
     * @param request the vehicle registration request
     * @return a success message with the chassis number
     * @throws IllegalStateException if the owner or chassis number is invalid
     */
    String registerVehicle(@Valid VehicleRequest request);

    /**
     * Retrieves all vehicles in the system.
     *
     * @return a list of all vehicles
     */
    List<Vehicle> getAll();

    /**
     * Searches for vehicles by chassis number or model name.
     *
     * @param searchTerm the term to search for
     * @return a list of matching vehicles
     * @throws IllegalArgumentException if the search term is invalid
     */
    List<Vehicle> search(String searchTerm);

    /**
     * Retrieves all vehicles owned by a specific owner.
     *
     * @param ownerId the ID of the owner
     * @return a list of vehicles owned by the owner
     * @throws IllegalStateException if the owner is not found
     */
    List<Vehicle> getByOwnerId(Long ownerId);
}