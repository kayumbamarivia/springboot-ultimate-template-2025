package com.spring.fortress.vehicles.config;

import com.spring.fortress.vehicles.utils.AuditLogEventListener;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.internal.SessionFactoryImpl;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

/**
 * {@code HibernateConfig} integrates custom Hibernate event listeners into the Spring context.
 * <p>
 * This configuration class registers an {@link AuditLogEventListener} to listen for specific Hibernate
 * entity lifecycle events — such as {@code POST_INSERT} and {@code POST_UPDATE}.
 * <p>
 * The listener is registered using the {@link EventListenerRegistry} obtained from the Hibernate
 * {@link SessionFactory}. This allows custom audit logging logic to be executed whenever an entity
 * is inserted or updated.
 *
 * <p><strong>Note:</strong> The use of {@code @PostConstruct} ensures the listener is
 * registered after the Spring context and EntityManagerFactory are fully initialized.
 *
 * @author Fortress Backend
 * @see com.spring.fortress.vehicles.utils.AuditLogEventListener
 */
@Configuration
@RequiredArgsConstructor
public class HibernateConfig {

    /**
     * The custom Hibernate listener that handles auditing logic for insert and update events.
     */
    private final AuditLogEventListener auditLogEventListener;

    /**
     * The JPA-provided entity manager factory, used to unwrap the native Hibernate {@link SessionFactory}.
     */
    private final EntityManagerFactory entityManagerFactory;

    /**
     * Registers the {@link AuditLogEventListener} with Hibernates internal event system.
     * <p>
     * The listener is added to handle {@code POST_INSERT} and {@code POST_UPDATE} events, enabling
     * automatic auditing for relevant entity changes.
     */
    @PostConstruct
    public void registerListeners() {
        // Unwrap the native Hibernate SessionFactory from the JPA-provided EntityManagerFactory
        SessionFactoryImpl sessionFactory = entityManagerFactory.unwrap(SessionFactoryImpl.class);

        // Obtain the event listener registry from the Hibernate service registry
        EventListenerRegistry registry = sessionFactory.getServiceRegistry().getService(EventListenerRegistry.class);

        // Register the audit listener for post-insert and post-update events
        assert registry != null;
        registry.appendListeners(EventType.POST_INSERT, auditLogEventListener);
        registry.appendListeners(EventType.POST_UPDATE, auditLogEventListener);
    }
}
