package com.spring.fortress.vehicles.interfaces;

import com.spring.fortress.vehicles.dtos.request.TransferRequest;
import com.spring.fortress.vehicles.models.Transfer;
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Interface defining vehicle ownership transfer operations in the vehicle tracking system.
 *
 * @author Fortress Backend
 * @since 1.0
 */
public interface TransferInterface {

    @Transactional
    String transferOwnerShip(TransferRequest req);

    /**
     * Transfers vehicle ownership from one owner to another.
     *
     * @param request the transfer request
     * @return a success message with the vehicle ID
     * @throws IllegalStateException if validation fails
     */
    String transferOwnership(@Valid TransferRequest request);

    /**
     * Retrieves all transfers in the system (admin-only).
     *
     * @return a list of all transfers
     */
    List<Transfer> getAll();

    /**
     * Retrieves the transfer history for a vehicle or owner.
     *
     * @param vehicleId the ID of the vehicle (optional)
     * @param ownerId   the ID of the owner (optional)
     * @return a list of transfers matching the criteria
     * @throws IllegalArgumentException if no valid criteria are provided
     */
    List<Transfer> getHistory(Long vehicleId, Long ownerId);
}