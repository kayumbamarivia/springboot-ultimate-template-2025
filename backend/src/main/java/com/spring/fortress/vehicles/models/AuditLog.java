package com.spring.fortress.vehicles.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entity class for the audit log system, storing records of changes to tracked entities.
 * <p>
 * This audit log captures information about actions performed on entities,
 * including what entity was affected, what action was performed, who performed it,
 * and when it occurred.
 * </p>
 * <p>
 * Used in conjunction with Hibernate event listeners to automatically track changes
 * to important entities in the system.
 * </p>
 *
 * @author Fortress Backend
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "HISTORY_TABLE", indexes = {
        @Index(name = "idx_audit_entity", columnList = "entity_type, entity_id"),
        @Index(name = "idx_audit_user", columnList = "user_email"),
        @Index(name = "idx_audit_date", columnList = "created_at")
})
@Data
public class AuditLog {
    /**
     * The unique identifier for the audit log entry.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The type/class name of the entity being audited.
     */
    @Column(name = "entity_type", nullable = false)
    private String entityType;

    /**
     * The ID of the entity being audited.
     */
    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    /**
     * The action performed on the entity (e.g., CREATE, UPDATE, DELETE).
     */
    @Column(name = "action", nullable = false)
    private String action;

    /**
     * The email of the user who performed the action.
     */
    @Column(name = "user_email", nullable = false)
    private String userEmail;

    /**
     * The timestamp when this audit log entry was created.
     * Automatically set to the current timestamp.
     */
    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

}