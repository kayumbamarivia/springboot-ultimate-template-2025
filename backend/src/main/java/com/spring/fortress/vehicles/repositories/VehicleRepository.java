package com.spring.fortress.vehicles.repositories;

import com.spring.fortress.vehicles.models.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing {@link Vehicle} entities.
 * Provides CRUD operations and custom queries for vehicles.
 *
 * @author Fortress Backend
 * @since 1.0
 */
@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    /**
     * Checks if a vehicle exists with the given chassis number.
     *
     * @param chassisNumber the chassis number to check
     * @return true if a vehicle exists, false otherwise
     */
    boolean existsByChassisNumber(String chassisNumber);

    /**
     * Finds all vehicles owned by the specified owner.
     *
     * @param ownerId the ID of the owner
     * @return a list of vehicles owned by the owner
     */
    List<Vehicle> findByOwnerId(Long ownerId);

    /**
     * Searches for vehicles by chassis number or model name (case-insensitive).
     *
     * @param searchTerm the term to search for
     * @return a list of matching vehicles
     */
    List<Vehicle> findByChassisNumberContainingIgnoreCaseOrModelNameContainingIgnoreCase(String searchTerm, String searchTerm2);
}