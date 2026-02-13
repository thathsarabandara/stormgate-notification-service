package com.thathsara.notification_service.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thathsara.notification_service.entities.NotificationTemplate;
import com.thathsara.notification_service.entities.NotificationTemplate.EventType;
import com.thathsara.notification_service.entities.NotificationTemplate.NotificationChannel;

/**
 * Repository interface for NotificationTemplate entity.
 */
@Repository
public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate, UUID> {

    /**
     * Find a template by tenant ID, event type, and channel.
     *
     * @param tenantId The tenant ID
     * @param eventType The event type
     * @param channel The notification channel
     * @return Optional template
     */
    Optional<NotificationTemplate> findByTenantIdAndEventTypeAndChannel(
            UUID tenantId,
            EventType eventType,
            NotificationChannel channel
    );

    /**
     * Find all enabled templates for a tenant and event type.
     *
     * @param tenantId The tenant ID
     * @param eventType The event type
     * @return List of templates
     */
    List<NotificationTemplate> findByTenantIdAndEventTypeAndIsEnabledTrue(
            UUID tenantId,
            EventType eventType
    );

    /**
     * Find all templates for a specific tenant.
     *
     * @param tenantId The tenant ID
     * @return List of templates
     */
    List<NotificationTemplate> findByTenantId(UUID tenantId);

    /**
     * Find all enabled templates for a tenant.
     *
     * @param tenantId The tenant ID
     * @return List of templates
     */
    List<NotificationTemplate> findByTenantIdAndIsEnabledTrue(UUID tenantId);
}
