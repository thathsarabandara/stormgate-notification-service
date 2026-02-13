package com.thathsara.notification_service.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.thathsara.notification_service.dtos.TemplateCreateUpdateRequest;
import com.thathsara.notification_service.dtos.TemplateResponse;
import com.thathsara.notification_service.entities.NotificationTemplate;
import com.thathsara.notification_service.entities.NotificationTemplate.EventType;
import com.thathsara.notification_service.entities.NotificationTemplate.NotificationChannel;
import com.thathsara.notification_service.repositories.NotificationTemplateRepository;

/**
 * Unit tests for NotificationTemplateService.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationTemplateService Tests")
class NotificationTemplateServiceTests {

    @Mock
    private NotificationTemplateRepository templateRepository;

    @InjectMocks
    private NotificationTemplateService templateService;

    private UUID tenantId;
    private UUID templateId;
    private NotificationTemplate template;
    private TemplateCreateUpdateRequest request;

    @BeforeEach
    void setUp() {
        tenantId = UUID.randomUUID();
        templateId = UUID.randomUUID();

        template = NotificationTemplate.builder()
                .id(templateId)
                .tenantId(tenantId)
                .eventType(EventType.USER_REGISTERED)
                .channel(NotificationChannel.EMAIL)
                .subject("Welcome")
                .body("Welcome to our platform")
                .isEnabled(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        request = TemplateCreateUpdateRequest.builder()
                .eventType("USER_REGISTERED")
                .channel("EMAIL")
                .subject("Welcome")
                .body("Welcome to our platform")
                .isEnabled(true)
                .build();
    }

    @Test
    @DisplayName("Should create a new template")
    void testCreateTemplate() {
        when(templateRepository.findByTenantIdAndEventTypeAndChannel(
                tenantId, EventType.USER_REGISTERED, NotificationChannel.EMAIL))
                .thenReturn(Optional.empty());
        when(templateRepository.save(any(NotificationTemplate.class)))
                .thenReturn(template);

        TemplateResponse response = templateService.createOrUpdateTemplate(tenantId, request);

        assertNotNull(response);
        assertEquals(templateId, response.getId());
        assertEquals(tenantId, response.getTenantId());
        assertEquals("USER_REGISTERED", response.getEventType());
        assertEquals("EMAIL", response.getChannel());
        verify(templateRepository, times(1)).save(any(NotificationTemplate.class));
    }

    @Test
    @DisplayName("Should update existing template")
    void testUpdateTemplate() {
        when(templateRepository.findByTenantIdAndEventTypeAndChannel(
                tenantId, EventType.USER_REGISTERED, NotificationChannel.EMAIL))
                .thenReturn(Optional.of(template));
        when(templateRepository.save(any(NotificationTemplate.class)))
                .thenReturn(template);

        request.setSubject("Updated Subject");
        request.setBody("Updated Body");

        TemplateResponse response = templateService.createOrUpdateTemplate(tenantId, request);

        assertNotNull(response);
        assertEquals(templateId, response.getId());
        verify(templateRepository, times(1)).save(any(NotificationTemplate.class));
    }

    @Test
    @DisplayName("Should get template by ID and tenant ID")
    void testGetTemplate() {
        when(templateRepository.findById(templateId))
                .thenReturn(Optional.of(template));

        TemplateResponse response = templateService.getTemplate(tenantId, templateId);

        assertNotNull(response);
        assertEquals(templateId, response.getId());
    }

    @Test
    @DisplayName("Should return null when template not found")
    void testGetTemplateNotFound() {
        when(templateRepository.findById(templateId))
                .thenReturn(Optional.empty());

        TemplateResponse response = templateService.getTemplate(tenantId, templateId);

        assertNull(response);
    }

    @Test
    @DisplayName("Should return null when tenant ID doesn't match")
    void testGetTemplateAccessDenied() {
        UUID differentTenantId = UUID.randomUUID();
        when(templateRepository.findById(templateId))
                .thenReturn(Optional.of(template));

        TemplateResponse response = templateService.getTemplate(differentTenantId, templateId);

        assertNull(response);
    }

    @Test
    @DisplayName("Should get all templates for tenant")
    void testGetTemplatesByTenant() {
        List<NotificationTemplate> templates = List.of(template);
        when(templateRepository.findByTenantId(tenantId))
                .thenReturn(templates);

        List<TemplateResponse> responses = templateService.getTemplatesByTenant(tenantId);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(templateId, responses.get(0).getId());
    }

    @Test
    @DisplayName("Should get enabled templates by event type")
    void testGetEnabledTemplates() {
        List<NotificationTemplate> templates = List.of(template);
        when(templateRepository.findByTenantIdAndEventTypeAndIsEnabledTrue(
                tenantId, EventType.USER_REGISTERED))
                .thenReturn(templates);

        List<TemplateResponse> responses = templateService.getEnabledTemplates(tenantId, "USER_REGISTERED");

        assertNotNull(responses);
        assertEquals(1, responses.size());
    }

    @Test
    @DisplayName("Should return empty list for invalid event type")
    void testGetEnabledTemplatesInvalidEventType() {
        List<TemplateResponse> responses = templateService.getEnabledTemplates(tenantId, "INVALID_EVENT");

        assertNotNull(responses);
        assertTrue(responses.isEmpty());
    }

    @Test
    @DisplayName("Should delete template successfully")
    void testDeleteTemplate() {
        when(templateRepository.findById(templateId))
                .thenReturn(Optional.of(template));

        boolean deleted = templateService.deleteTemplate(tenantId, templateId);

        assertTrue(deleted);
        verify(templateRepository, times(1)).deleteById(templateId);
    }

    @Test
    @DisplayName("Should not delete non-existent template")
    void testDeleteTemplateNotFound() {
        when(templateRepository.findById(templateId))
                .thenReturn(Optional.empty());

        boolean deleted = templateService.deleteTemplate(tenantId, templateId);

        assertFalse(deleted);
        verify(templateRepository, never()).deleteById(templateId);
    }

    @Test
    @DisplayName("Should not delete template from different tenant")
    void testDeleteTemplateAccessDenied() {
        UUID differentTenantId = UUID.randomUUID();
        when(templateRepository.findById(templateId))
                .thenReturn(Optional.of(template));

        boolean deleted = templateService.deleteTemplate(differentTenantId, templateId);

        assertFalse(deleted);
        verify(templateRepository, never()).deleteById(templateId);
    }

    @Test
    @DisplayName("Should get empty list when no templates found")
    void testGetTemplatesByTenantEmpty() {
        when(templateRepository.findByTenantId(tenantId))
                .thenReturn(List.of());

        List<TemplateResponse> responses = templateService.getTemplatesByTenant(tenantId);

        assertNotNull(responses);
        assertTrue(responses.isEmpty());
    }
}
