package com.spring.fortress.vehicles.models;

import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity class representing a vehicle owner.
 * <p>
 * This class connects a user to their owned vehicles and license plates.
 * It serves as the pivotal relationship point in the ownership management system,
 * allowing one user to own multiple vehicles and plates.
 * </p>
 *
 * @author Fortress Backend
 * @version 1.0
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "owners")
public class Owner {
    /**
     * The unique identifier for the owner.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * The user associated with this owner profile.
     * Each owner must be linked to exactly one user.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * The collection of license plates owned by this owner.
     * Uses a Set to prevent duplicates.
     */
    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Builder.Default
    private Set<Plate> plates = new HashSet<>();

    /**
     * The collection of vehicles owned by this owner.
     * Uses a HashSet to prevent duplicates and ensure efficient lookups.
     */
    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Builder.Default
    private Set<Vehicle> vehicles = new HashSet<>();

    /**
     * Adds a vehicle to this owner's collection.
     * Also sets the owner reference in the vehicle.
     *
     * @param vehicle the vehicle to add
     * @return true if the vehicle was added, false if it was already present
     */
    public boolean addVehicle(Vehicle vehicle) {
        vehicle.setOwner(this);
        return vehicles.add(vehicle);
    }

    /**
     * Adds a plate to this owner's collection.
     * Also sets the owner reference in the plate.
     *
     * @param plate the plate to add
     * @return true if the plate was added, false if it was already present
     */
    public boolean addPlate(Plate plate) {
        plate.setOwner(this);
        return plates.add(plate);
    }
}