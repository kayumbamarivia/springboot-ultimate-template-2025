package com.spring.fortress.vehicles.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.fortress.vehicles.models.AuditLog;
import com.spring.fortress.vehicles.repositories.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.event.spi.*;
import org.hibernate.persister.entity.EntityPersister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * Hibernate event listener for auditing entity creation and updates.
 * Logs audit events to the HISTORY table with entity details and user information.
 *
 * @author Fortress Backend
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
public class AuditLogEventListener implements PostInsertEventListener, PostUpdateEventListener {
    private static final Logger log = LoggerFactory.getLogger(AuditLogEventListener.class);
    private final ObjectMapper objectMapper;
    private final AuditLogRepository auditLogRepository;

    /**
     * Handles post-insert events by logging entity creation details.
     *
     * @param event the post-insert event
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onPostInsert(PostInsertEvent event) {
        if (isAuditLogEntity(event.getEntity())) {
            log.debug("Skipping audit for AuditLog entity");
            return;
        }
        logAuditEvent(event.getEntity(), event.getId(), event.getPersister(), "CREATE", null, event.getState());
    }

    /**
     * Handles post-update events by logging entity update details.
     *
     * @param event the post-update event
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onPostUpdate(PostUpdateEvent event) {
        if (isAuditLogEntity(event.getEntity())) {
            return;
        }
        logAuditEvent(event.getEntity(), event.getId(), event.getPersister(), "UPDATE", event.getOldState(), event.getState());
    }

    /**
     * Checks if the entity is an AuditLog to prevent recursive auditing.
     *
     * @param entity the entity to check
     * @return true if the entity is an AuditLog
     */
    private boolean isAuditLogEntity(Object entity) {
        return entity instanceof AuditLog;
    }

    /**
     * Logs an audit event with entity details, user, and state changes.
     *
     * @param entity    the entity being audited
     * @param id        the entity ID
     * @param persist the entity persist
     * @param action    the action type (CREATE or UPDATE)
     * @param oldState  the previous state (for updates)
     * @param newState  the new state
     */
    private void logAuditEvent(Object entity, Object id, EntityPersister persist, String action, Object[] oldState, Object[] newState) {
        try {
            String entityType = entity.getClass().getSimpleName();
            String userEmail = getCurrentUser(entity, action, persist, newState);
            String oldValues = oldState != null ? toJson(persist.getPropertyNames(), oldState) : null;
            String newValues = toJson(persist.getPropertyNames(), newState);

            AuditLog auditLog = new AuditLog();
            auditLog.setEntityType(entityType);
            auditLog.setEntityId(((Number) id).longValue());
            auditLog.setAction(action);
            auditLog.setUserEmail(userEmail);

            log.debug("Saving audit log for {} {} by {}", action, entityType, userEmail);
            auditLogRepository.save(auditLog);
            log.info("Logged audit event: {} {} by {}", action, entityType, userEmail);
        } catch (Exception e) {
            log.error("Failed to log audit event for {}: {}", entity.getClass().getSimpleName(), e.getMessage(), e);
            throw new RuntimeException("Audit logging failed", e); // Ensure transaction rolls back
        }
    }

    /**
     * Retrieves the email of the currently authenticated user or extracts it from the entity during registration.
     *
     * @param entity    the entity being processed
     * @param action    the action type (CREATE or UPDATE)
     * @param persist the entity persist
     * @param state     the current state of the entity
     * @return the user's email or "system" if not available
     */
    private String getCurrentUser(Object entity, String action, EntityPersister persist, Object[] state) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !authentication.getPrincipal().equals("anonymousUser")) {
            return authentication.getName();
        }

        // For user registration (CREATE action on User entity), extract email from the entity
        if ("CREATE".equals(action) && entity.getClass().getSimpleName().equals("User") && state != null) {
            String[] propertyNames = persist.getPropertyNames();
            for (int i = 0; i < propertyNames.length && i < state.length; i++) {
                if ("email".equalsIgnoreCase(propertyNames[i]) && state[i] != null) {
                    return state[i].toString();
                }
            }
        }

        return "system";
    }

    /**
     * Converts entity state to JSON for audit logging.
     *
     * @param propertyNames the property names of the entity
     * @param state         the state values
     * @return the JSON string or null if state is null
     * @throws JsonProcessingException if JSON serialization fails
     */
    private String toJson(String[] propertyNames, Object[] state) throws JsonProcessingException {
        if (state == null) {
            return null;
        }
        Map<String, Object> map = new HashMap<>();
        for (int i = 0; i < propertyNames.length && i < state.length; i++) {
            if (state[i] != null) {
                map.put(propertyNames[i], state[i]);
            }
        }
        return objectMapper.writeValueAsString(map);
    }

    /**
     * Indicates whether post-commit handling is required.
     *
     * @param persist the entity persist
     * @return {@code false} as no post-commit handling is needed
     */
    @Override
    public boolean requiresPostCommitHandling(EntityPersister persist) {
        return false;
    }
}