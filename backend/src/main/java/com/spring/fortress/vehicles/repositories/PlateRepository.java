package com.spring.fortress.vehicles.repositories;

import com.spring.fortress.vehicles.models.Plate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing {@link Plate} entities.
 * Provides CRUD operations and custom queries for vehicle license plates.
 *
 * @author Fortress Backend
 * @since 1.0
 */
@Repository
public interface PlateRepository extends JpaRepository<Plate, Long> {

    /**
     * Checks if a plate exists with the given plate number.
     *
     * @param plateNumber the plate number to check
     * @return true if a plate exists, false otherwise
     */
    boolean existsByPlateNumber(String plateNumber);

    /**
     * Finds all plates owned by the specified owner.
     *
     * @param ownerId the ID of the owner
     * @return a list of plates owned by the owner
     */
    List<Plate> findByOwnerId(Long ownerId);
}