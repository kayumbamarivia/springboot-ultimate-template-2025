package com.spring.fortress.vehicles.services;

import com.spring.fortress.vehicles.dtos.request.TransferRequest;
import com.spring.fortress.vehicles.interfaces.TransferInterface;
import com.spring.fortress.vehicles.models.Owner;
import com.spring.fortress.vehicles.models.Transfer;
import com.spring.fortress.vehicles.models.Vehicle;
import com.spring.fortress.vehicles.repositories.OwnerRepository;
import com.spring.fortress.vehicles.repositories.TransferRepository;
import com.spring.fortress.vehicles.repositories.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Validated
public class TransferService implements TransferInterface {

    private final TransferRepository transferRepository;
    private final VehicleRepository vehicleRepository;
    private final OwnerRepository ownerRepository;
    private final EmailService emailService;

    @Override
    @Transactional
    public String transferOwnerShip(TransferRequest req) {
        log.info("Processing ownership transfer for vehicle ID: {}", req.vehicleId());

        // Validate vehicle exists
        Vehicle vehicle = vehicleRepository.findById(req.vehicleId())
                .orElseThrow(() -> {
                    log.error("Vehicle with ID {} not found", req.vehicleId());
                    return new IllegalStateException("Vehicle not found");
                });

        // Validate old owner
        if (!ownerRepository.existsById(req.oldOwnerId())) {
            log.error("Old owner with ID {} not found", req.oldOwnerId());
            throw new IllegalStateException("Old owner not found");
        }

        // Validate new owner
        if (!ownerRepository.existsById(req.newOwnerId())) {
            log.error("New owner with ID {} not found", req.newOwnerId());
            throw new IllegalStateException("New owner not found");
        }

        // Verify current ownership
        if (!(Objects.equals(vehicle.getOwner().getId(), req.oldOwnerId()))) {
            log.error("Vehicle {} is not owned by old owner {}", req.vehicleId(), req.oldOwnerId());
            throw new IllegalStateException("Vehicle is not owned by the specified old owner");
        }

        // Create transfer record
        Owner oldOwner = ownerRepository.findById(req.oldOwnerId()).orElseThrow(null);
        Owner newOwner = ownerRepository.findById(req.newOwnerId()).orElseThrow(null);
        Vehicle vehicleToTransfer = vehicleRepository.findById(req.vehicleId()).orElseThrow(null);
        Transfer transfer = Transfer.builder()
                .issuedDate(req.issuedDate())
                .oldOwner(oldOwner)
                .newOwner(newOwner)
                .amount(req.amount())
                .vehicle(vehicleToTransfer)
                .build();

        // Update vehicle ownership
        vehicle.setOwner(newOwner);

        // Save changes
        transferRepository.save(transfer);
        vehicleRepository.save(vehicle);

        // Send email notifications
        try {
            String oldOwnerEmail = Optional.ofNullable(ownerRepository.findEmailById(req.oldOwnerId()))
                    .map(String::strip)
                    .orElse(null);

            String newOwnerEmail = Optional.ofNullable(ownerRepository.findEmailById(req.newOwnerId()))
                    .map(String::strip)
                    .orElse(null);

            emailService.sendTransferNotification(oldOwnerEmail, newOwnerEmail, vehicle);
            log.info("Transfer notifications sent successfully for vehicle ID: {}", req.vehicleId());
        } catch (Exception e) {
            log.error("Failed to send transfer notifications: {}", e.getMessage());
        }

        log.info("Successfully transferred ownership for vehicle ID: {}", req.vehicleId());
        return "Ownership transferred successfully for vehicle ID: " + req.vehicleId();
    }

    @Override
    public String transferOwnership(TransferRequest request) {
        return "";
    }

    @Override
    public List<Transfer> getAll() {
        return List.of();
    }

    @Override
    public List<Transfer> getHistory(Long vehicleId, Long ownerId) {
        return List.of();
    }
}
