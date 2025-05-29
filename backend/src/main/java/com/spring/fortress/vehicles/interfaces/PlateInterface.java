package com.spring.fortress.vehicles.interfaces;

import com.spring.fortress.vehicles.dtos.request.PlateRequest;
import com.spring.fortress.vehicles.models.Plate;
import jakarta.validation.Valid;

import java.util.List;

/**
 * Interface defining vehicle license plate operations in the vehicle tracking system.
 *
 * @author Fortress Backend
 * @since 1.0
 */
public interface PlateInterface {

    /**
     * Registers a new license plate.
     *
     * @param request the plate registration request
     * @return a success message with the plate number
     * @throws IllegalStateException if validation fails
     */
    String registerPlate(@Valid PlateRequest request);

    /**
     * Retrieves all plates for a specific owner.
     *
     * @param ownerId the ID of the owner
     * @return a list of plates owned by the owner
     * @throws IllegalStateException if the owner is not found
     */
    List<Plate> getByOwnerId(Long ownerId);
    List<Plate> getAll();
}