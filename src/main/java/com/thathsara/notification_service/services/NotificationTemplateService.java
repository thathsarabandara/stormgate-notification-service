package com.thathsara.notification_service.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thathsara.notification_service.dtos.TemplateCreateUpdateRequest;
import com.thathsara.notification_service.dtos.TemplateResponse;
import com.thathsara.notification_service.entities.NotificationTemplate;
import com.thathsara.notification_service.entities.NotificationTemplate.EventType;
import com.thathsara.notification_service.entities.NotificationTemplate.NotificationChannel;
import com.thathsara.notification_service.repositories.NotificationTemplateRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * Service for managing notification templates.
 * Allows tenants to create, update, and manage their notification templates.
 */
@Slf4j
@Service
public class NotificationTemplateService {

    /**
     * Template repository.
     */
    @Autowired
    private NotificationTemplateRepository templateRepository;

    /**
     * Create or update a notification template.
     *
     * @param tenantId The tenant ID
     * @param request The template create/update request
     * @return The created or updated template
     */
    @Transactional
    public TemplateResponse createOrUpdateTemplate(UUID tenantId, TemplateCreateUpdateRequest request) {
        log.info("Creating/updating template for tenant: {} event: {} channel: {}",
                tenantId, request.getEventType(), request.getChannel());

        final Optional<NotificationTemplate> existing = templateRepository
                .findByTenantIdAndEventTypeAndChannel(
                        tenantId,
                        EventType.valueOf(request.getEventType()),
                        NotificationChannel.valueOf(request.getChannel())
                );

        final NotificationTemplate template;
        if (existing.isPresent()) {
            template = existing.get();
            template.setSubject(request.getSubject());
            template.setBody(request.getBody());
            template.setIsEnabled(request.getIsEnabled());
        } else {
            template = NotificationTemplate.builder()
                    .tenantId(tenantId)
                    .eventType(EventType.valueOf(request.getEventType()))
                    .channel(NotificationChannel.valueOf(request.getChannel()))
                    .subject(request.getSubject())
                    .body(request.getBody())
                    .isEnabled(request.getIsEnabled())
                    .build();
        }

        final NotificationTemplate saved = templateRepository.save(template);
        log.info("Template created/updated successfully with ID: {}", saved.getId());

        return mapToResponse(saved);
    }

    /**
     * Get a specific template.
     *
     * @param tenantId The tenant ID
     * @param templateId The template ID
     * @return The template response
     */
    public TemplateResponse getTemplate(UUID tenantId, UUID templateId) {
        final Optional<NotificationTemplate> template = templateRepository.findById(templateId);

        if (template.isEmpty() || !template.get().getTenantId().equals(tenantId)) {
            log.warn("Template not found or access denied for tenant: {} template: {}", tenantId, templateId);
            return null;
        }

        return mapToResponse(template.get());
    }

    /**
     * Get all templates for a tenant.
     *
     * @param tenantId The tenant ID
     * @return List of template responses
     */
    public List<TemplateResponse> getTemplatesByTenant(UUID tenantId) {
        final List<NotificationTemplate> templates = templateRepository.findByTenantId(tenantId);
        return templates.stream().map(this::mapToResponse).toList();
    }

    /**
     * Get all enabled templates for a tenant and event type.
     *
     * @param tenantId The tenant ID
     * @param eventType The event type
     * @return List of enabled template responses
     */
    public List<TemplateResponse> getEnabledTemplates(UUID tenantId, String eventType) {
        try {
            final List<NotificationTemplate> templates = templateRepository
                    .findByTenantIdAndEventTypeAndIsEnabledTrue(tenantId, EventType.valueOf(eventType));
            return templates.stream().map(this::mapToResponse).toList();
        } catch (IllegalArgumentException e) {
            log.error("Invalid event type: {}", eventType);
            return List.of();
        }
    }

    /**
     * Delete a template.
     *
     * @param tenantId The tenant ID
     * @param templateId The template ID
     * @return true if deleted, false otherwise
     */
    @Transactional
    public boolean deleteTemplate(UUID tenantId, UUID templateId) {
        final Optional<NotificationTemplate> template = templateRepository.findById(templateId);

        if (template.isEmpty() || !template.get().getTenantId().equals(tenantId)) {
            log.warn("Template not found or access denied for deletion: {}", templateId);
            return false;
        }

        templateRepository.deleteById(templateId);
        log.info("Template deleted successfully: {}", templateId);
        return true;
    }

    /**
     * Map entity to DTO response.
     *
     * @param template The template entity
     * @return The template response DTO
     */
    private TemplateResponse mapToResponse(NotificationTemplate template) {
        return TemplateResponse.builder()
                .id(template.getId())
                .tenantId(template.getTenantId())
                .eventType(template.getEventType().toString())
                .channel(template.getChannel().toString())
                .subject(template.getSubject())
                .body(template.getBody())
                .isEnabled(template.getIsEnabled())
                .createdAt(template.getCreatedAt())
                .updatedAt(template.getUpdatedAt())
                .build();
    }
}
