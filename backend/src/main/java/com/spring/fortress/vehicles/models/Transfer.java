package com.spring.fortress.vehicles.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entity class representing a transfer of vehicle ownership.
 * <p>
 * This class tracks the transfer of a vehicle from one owner to another,
 * including the date of transfer, the previous owner, new owner, amount paid,
 * and the vehicle being transferred.
 * </p>
 * <p>
 * Transfer records are essential for maintaining a complete audit trail of
 * vehicle ownership history in the system.
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
@Table(name = "transfers", indexes = {
        @Index(name = "idx_transfer_vehicle", columnList = "vehicle_id"),
        @Index(name = "idx_transfer_date", columnList = "issued_date")
})
public class Transfer {
    /**
     * The unique identifier for the transfer record.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * The date and time when the transfer was issued/completed.
     * Automatically set to the current timestamp.
     */
    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "issued_date", nullable = false, updatable = false)
    private LocalDateTime issuedDate;

    /**
     * The previous owner of the vehicle.
     */
    @OneToOne
    @JoinColumn(name = "old_owner_id", nullable = false)
    private Owner oldOwner;

    /**
     * The new owner of the vehicle.
     */
    @OneToOne
    @JoinColumn(name = "new_owner_id", nullable = false)
    private Owner newOwner;

    /**
     * The amount paid for the transfer/sale of the vehicle.
     */
    @Column(name = "amount", nullable = false)
    private Double amount;

    /**
     * The vehicle being transferred.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;
}