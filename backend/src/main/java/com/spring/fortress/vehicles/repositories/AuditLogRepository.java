package com.spring.fortress.vehicles.repositories;

import com.spring.fortress.vehicles.models.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing {@link AuditLog} entities.
 * <p>
 * Provides basic CRUD operations, as well as pagination and query support,
 * through Spring Data JPA.
 *
 * <p>No additional method definitions are required unless custom queries are needed.
 *
 * @author Fortress Backend
 * @see com.spring.fortress.vehicles.models.AuditLog
 */
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}
