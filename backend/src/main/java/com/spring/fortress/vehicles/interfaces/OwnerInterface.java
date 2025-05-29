package com.spring.fortress.vehicles.interfaces;

import com.spring.fortress.vehicles.dtos.request.OwnerRequest;
import com.spring.fortress.vehicles.models.Owner;
import com.spring.fortress.vehicles.models.User;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

/**
 * Interface defining owner-related operations in the vehicle tracking system.
 *
 * @author Fortress Backend
 * @since 1.0
 */
public interface OwnerInterface {

    /**
     * Registers a new owner.
     *
     * @param request the owner registration request
     * @return a success message with the user ID
     * @throws IllegalStateException if validation fails
     */
    String registerOwner(@Valid OwnerRequest request);

    /**
     * Retrieves all owners in the system (admin-only).
     *
     * @return a list of all owners
     */
    List<User> getAll();

    /**
     * Searches for owners by email or name.
     *
     * @param searchTerm the term to search for
     * @return a list of matching owners
     * @throws IllegalArgumentException if the search term is invalid
     */
    List<User> search(String searchTerm);
}