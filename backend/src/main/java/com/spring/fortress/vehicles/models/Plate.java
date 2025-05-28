package com.spring.fortress.vehicles.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spring.fortress.vehicles.enums.PlateStatus;
import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

/**
 * Entity class representing a vehicle license plate.
 * <p>
 * This class manages license plates that can be assigned to vehicles and tracks
 * their status (available, assigned, etc.), issue date, owner, and the vehicle
 * it's assigned to.
 * </p>
 *
 * @author Fortress Backend
 * @version 1.0
 * @since 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "plates", indexes = {
        @Index(name = "idx_plate_number", columnList = "plate_number", unique = true),
        @Index(name = "idx_plate_status", columnList = "status")
})
public class Plate {
    /**
     * The unique identifier for the plate.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * The plate number - must be unique.
     */
    @Column(name = "plate_number", nullable = false, unique = true)
    private String plateNumber;

    /**
     * The current status of the plate (available, assigned, etc.).
     * Default status is AVAILABLE.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PlateStatus status = PlateStatus.AVAILABLE;

    /**
     * The date and time when the plate was issued.
     * Automatically set to the current timestamp.
     */
    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "issued_date", nullable = false, updatable = false)
    private LocalDateTime issuedDate;

    /**
     * The owner of the plate.
     */
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "owner_id")
    private Owner owner;

    /**
     * The vehicle to which the plate is assigned.
     */
    @OneToOne
    @JoinColumn(name = "vehicle_id", nullable = true)
    private Vehicle vehicle;
}