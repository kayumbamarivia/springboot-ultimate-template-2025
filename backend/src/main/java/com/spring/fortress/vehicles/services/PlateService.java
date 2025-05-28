package com.spring.fortress.vehicles.services;

import com.spring.fortress.vehicles.dtos.request.PlateRequest;
import com.spring.fortress.vehicles.interfaces.PlateInterface;
import com.spring.fortress.vehicles.models.Owner;
import com.spring.fortress.vehicles.models.Plate;
import com.spring.fortress.vehicles.models.Vehicle;
import com.spring.fortress.vehicles.repositories.PlateRepository;
import com.spring.fortress.vehicles.repositories.OwnerRepository;
import com.spring.fortress.vehicles.repositories.VehicleRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * Service for managing vehicle license plate registration in the vehicle tracking system.
 * Handles creation of new plates with validation and persistence.
 *
 * @author Fortress Backend
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Validated
public class PlateService implements PlateInterface {

    private final PlateRepository plateRepository;
    private final OwnerRepository ownerRepository;
    private final VehicleRepository vehicleRepository;

    /**
     * Registers a new license plate based on the provided request.
     * Validates the owner, vehicle, and plate number before saving.
     *
     * @param request the plate registration request
     * @return a success message with the plate number
     * @throws IllegalStateException if validation fails
     */
    @Override
    @Transactional
    public String registerPlate(@Valid PlateRequest request) {
        log.info("Registering plate with number: {}", request.plateNumber());

        // Validate owner
        Owner owner = ownerRepository.findById(request.ownerId())
                .orElseThrow(() -> {
                    log.error("Owner with ID {} not found", request.ownerId());
                    return new IllegalStateException("Owner with ID " + request.ownerId() + " not found");
                });

        // Validate vehicle
        Vehicle vehicle = vehicleRepository.findById(request.vehicleId())
                .orElseThrow(() -> {
                    log.error("Vehicle with ID {} not found", request.vehicleId());
                    return new IllegalStateException("Vehicle with ID " + request.vehicleId() + " not found");
                });

        // Check for duplicate plate number
        if (plateRepository.existsByPlateNumber(request.plateNumber())) {
            log.warn("Plate number {} already exists", request.plateNumber());
            throw new IllegalStateException("Plate number " + request.plateNumber() + " already exists");
        }

        // Create and save plate entity
        Plate plate = Plate.builder()
                .plateNumber(request.plateNumber())
                .status(request.status())
                .issuedDate(request.issuedDate())
                .owner(owner)
                .vehicle(vehicle)
                .build();

        plateRepository.save(plate);
        log.info("Successfully registered plate with number: {}", request.plateNumber());

        return "Plate registered successfully with number: " + request.plateNumber();
    }

    @Override
    public List<Plate> getByOwnerId(Long ownerId) {
        return List.of();
    }
}