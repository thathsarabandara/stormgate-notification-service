package com.thathsara.notification_service.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thathsara.notification_service.dtos.TemplateCreateUpdateRequest;
import com.thathsara.notification_service.dtos.TemplateResponse;
import com.thathsara.notification_service.services.NotificationTemplateService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller for notification template management.
 * Admin only endpoint for managing templates per tenant.
 */
@Slf4j
@RestController
@RequestMapping("/templates")
public class NotificationTemplateController {

    /**
     * Template service.
     */
    @Autowired
    private NotificationTemplateService templateService;

    /**
     * Create or update a notification template.
     *
     * @param request The template creation/update request
     * @param authentication The authentication object
     * @return Response with created/updated template
     */
    @PostMapping
    public ResponseEntity<TemplateResponse> createOrUpdateTemplate(
            @Valid @RequestBody TemplateCreateUpdateRequest request,
            Authentication authentication) {

        log.info("Creating/updating template for tenant with user: {}", authentication.getName());

        try {
            // Extract tenant ID from authentication (implementation depends on your token)
            final UUID tenantId = (UUID) authentication.getCredentials();

            final TemplateResponse response = templateService.createOrUpdateTemplate(tenantId, request);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Error creating/updating template", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get all templates for the tenant.
     *
     * @param authentication The authentication object
     * @return List of templates
     */
    @GetMapping
    public ResponseEntity<List<TemplateResponse>> getTemplates(Authentication authentication) {
        log.info("Fetching templates for tenant with user: {}", authentication.getName());

        try {
            final UUID tenantId = (UUID) authentication.getCredentials();

            final List<TemplateResponse> templates = templateService.getTemplatesByTenant(tenantId);

            return ResponseEntity.ok(templates);
        } catch (Exception e) {
            log.error("Error fetching templates", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get a specific template.
     *
     * @param templateId The template ID
     * @param authentication The authentication object
     * @return The template response
     */
    @GetMapping("/{templateId}")
    public ResponseEntity<TemplateResponse> getTemplate(
            @PathVariable UUID templateId,
            Authentication authentication) {

        log.info("Fetching template: {}", templateId);

        try {
            final UUID tenantId = (UUID) authentication.getCredentials();

            final TemplateResponse template = templateService.getTemplate(tenantId, templateId);

            if (template == null) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(template);
        } catch (Exception e) {
            log.error("Error fetching template", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Delete a template.
     *
     * @param templateId The template ID
     * @param authentication The authentication object
     * @return Response
     */
    @DeleteMapping("/{templateId}")
    public ResponseEntity<Void> deleteTemplate(
            @PathVariable UUID templateId,
            Authentication authentication) {

        log.info("Deleting template: {}", templateId);

        try {
            final UUID tenantId = (UUID) authentication.getCredentials();

            final boolean deleted = templateService.deleteTemplate(tenantId, templateId);

            if (!deleted) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error deleting template", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
