package com.spring.fortress.vehicles.services;

import com.spring.fortress.vehicles.dtos.request.OwnerRequest;
import com.spring.fortress.vehicles.interfaces.OwnerInterface;
import com.spring.fortress.vehicles.models.Owner;
import com.spring.fortress.vehicles.models.User;
import com.spring.fortress.vehicles.repositories.OwnerRepository;
import com.spring.fortress.vehicles.repositories.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing owner registration in the vehicle tracking system.
 * Handles creation of new owners linked to users.
 *
 * @author Fortress Backend
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Validated
public class OwnerService implements OwnerInterface {

    private final OwnerRepository ownerRepository;
    private final UserRepository userRepository;

    /**
     * Registers a new owner based on the provided request.
     * Validates the user and checks for duplicate owners before saving.
     *
     * @param request the owner registration request
     * @return a success message with the user ID
     * @throws IllegalStateException if validation fails
     */
    @Override
    @Transactional
    public String registerOwner(@Valid OwnerRequest request) {
        log.info("Registering owner with user ID: {}", request.userId());

        // Check for duplicate owner
        if (ownerRepository.existsByUserId(request.userId())) {
            log.warn("Owner with user ID {} already exists", request.userId());
            throw new IllegalStateException("Owner with user ID " + request.userId() + " already exists");
        }

        // Validate user
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> {
                    log.error("User with email {} not found", request.email());
                    return new IllegalStateException("User with email " + request.email() + " not found");
                });

        // Verify user ID matches email
        if (!user.getId().equals(request.userId())) {
            log.error("User ID {} does not match email {}", request.userId(), request.email());
            throw new IllegalStateException("User ID does not match provided email");
        }

        // Create and save owner entity
        Owner owner = Owner.builder()
                .user(user)
                .build();

        ownerRepository.save(owner);
        log.info("Successfully registered owner with user ID: {}", request.userId());

        return "Owner registered successfully with user ID: " + request.userId();
    }

    @Override
    public List<Owner> getAll() {
        return List.of();
    }

    @Override
    public List<Owner> search(String searchTerm) {
        return List.of();
    }

    @Override
    public Optional<Owner> getById(Long id) {
        return Optional.empty();
    }
}