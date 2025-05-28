package com.spring.fortress.vehicles.repositories;

import com.spring.fortress.vehicles.models.Owner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing {@link Owner} entities.
 * Provides CRUD operations and custom queries for owners.
 *
 * @author Fortress Backend
 * @since 1.0
 */
@Repository
public interface OwnerRepository extends JpaRepository<Owner, Long> {

    /**
     * Checks if an owner exists with the given user ID.
     *
     * @param userId the user ID to check
     * @return true if an owner exists, false otherwise
     */
    boolean existsByUserId(Long userId);

    /**
     * Finds the email address of an owner by their ID.
     *
     * @param id the ID of the owner
     * @return the owner's email address, or null if not found
     */
    @Query("SELECT o.user.email FROM Owner o WHERE o.id = ?1")
    String findEmailById(Long id);

    /**
     * Searches for owners by email or name (case-insensitive).
     *
     * @param searchTerm the term to search for
     * @return a list of matching owners
     */
    @Query("SELECT o FROM Owner o WHERE LOWER(o.user.email) LIKE LOWER(CONCAT('%', ?1, '%')) OR LOWER(o.user.firstName) LIKE LOWER(CONCAT('%', ?1, '%')) OR LOWER(o.user.lastName) LIKE LOWER(CONCAT('%', ?1, '%'))")
    List<Owner> findByUserEmailContainingIgnoreCaseOrUserFirstNameContainingIgnoreCaseOrUserLastNameContainingIgnoreCase(String searchTerm);
}