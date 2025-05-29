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
    List<Transfer> findByVehicleIdOrNewOwnerId(Long vehicleId, Long ownerId);
}