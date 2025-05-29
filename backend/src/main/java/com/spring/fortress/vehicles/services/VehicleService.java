package com.spring.fortress.vehicles.services;

import com.spring.fortress.vehicles.dtos.request.VehicleRequest;
import com.spring.fortress.vehicles.interfaces.VehicleInterface;
import com.spring.fortress.vehicles.models.Owner;
import com.spring.fortress.vehicles.models.Vehicle;
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
 * Service for managing vehicle registration in the vehicle tracking system.
 * Handles creation of new vehicles with validation and persistence.
 *
 * @author Fortress Backend
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Validated
public class VehicleService implements VehicleInterface {

    private final VehicleRepository vehicleRepository;
    private final OwnerRepository ownerRepository;

    /**
     * Registers a new vehicle based on the provided request.
     * Validates the owner and chassis number before saving the vehicle.
     *
     * @param request the vehicle registration request
     * @return a success message with the chassis number
     * @throws IllegalStateException if the owner or chassis number is invalid
     */
    @Override
    @Transactional
    public String registerVehicle(@Valid VehicleRequest request) {
        log.info("Registering vehicle with chassis number: {}", request.chassisNumber());

        // Validate owner existence
        Owner owner = ownerRepository.findById(request.ownerId())
                .orElseThrow(() -> {
                    log.error("Owner with ID {} not found", request.ownerId());
                    return new IllegalStateException("Owner with ID " + request.ownerId() + " not found");
                });

        // Check for duplicate chassis number
        if (vehicleRepository.existsByChassisNumber(request.chassisNumber())) {
            log.warn("Vehicle with chassis number {} already exists", request.chassisNumber());
            throw new IllegalStateException("Vehicle with chassis number " + request.chassisNumber() + " already exists");
        }

        // Create and save vehicle entity
        Vehicle vehicle = Vehicle.builder()
                .chassisNumber(request.chassisNumber())
                .manufacturer(request.manufacturer())
                .manufacturedYear(request.manufacturedYear())
                .price(request.price())
                .modelName(request.modelName())
                .owner(owner)
                .build();

        vehicleRepository.save(vehicle);
        log.info("Successfully registered vehicle with chassis number: {}", request.chassisNumber());

        return "Vehicle registered successfully with chassis number: " + request.chassisNumber();
    }

    @Override
    public List<Vehicle> getAll() {
        return vehicleRepository.findAll();
    }

    @Override
    public List<Vehicle> search(String searchTerm) {
        return vehicleRepository.findByChassisNumberContainingIgnoreCaseOrModelNameContainingIgnoreCase(searchTerm, searchTerm);
    }

    @Override
    public List<Vehicle> getByOwnerId(Long ownerId) {
        return vehicleRepository.findByOwnerId(ownerId);
    }
}