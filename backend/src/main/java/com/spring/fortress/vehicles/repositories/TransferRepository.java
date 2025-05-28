package com.spring.fortress.vehicles.repositories;

import com.spring.fortress.vehicles.models.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing {@link Transfer} entities.
 * Provides CRUD operations and custom queries for vehicle ownership transfers.
 *
 * @author Fortress Backend
 * @since 1.0
 */
@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {

    /**
     * Finds all transfers for a specific vehicle.
     *
     * @param vehicleId the ID of the vehicle
     * @return a list of transfers for the vehicle
     */
    List<Transfer> findByVehicleId(Long vehicleId);

    /**
     * Finds all transfers involving a specific owner (as old or new owner).
     *
     * @param ownerId the ID of the owner
     * @return a list of transfers involving the owner
     */
    List<Transfer> findByOldOwnerIdOrNewOwnerId(Long ownerId, Long ownerId2);
}