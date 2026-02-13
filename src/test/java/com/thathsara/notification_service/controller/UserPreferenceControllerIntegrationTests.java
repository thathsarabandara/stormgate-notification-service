package com.thathsara.notification_service.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thathsara.notification_service.dtos.UserPreferenceRequest;
import com.thathsara.notification_service.dtos.UserPreferenceResponse;
import com.thathsara.notification_service.services.UserPreferenceService;

/**
 * Integration tests for UserPreferenceController.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("UserPreferenceController Integration Tests")
class UserPreferenceControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    private UserPreferenceService preferenceService;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID userId;
    private UUID tenantId;
    private UserPreferenceResponse preferenceResponse;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        tenantId = UUID.randomUUID();

        preferenceResponse = UserPreferenceResponse.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .tenantId(tenantId)
                .emailEnabled(true)
                .smsEnabled(true)
                .pushEnabled(false)
                .inAppEnabled(true)
                .frequency("IMMEDIATE")
                .build();
    }

    @Test
    @DisplayName("Should get user preferences")
    void testGetPreferences() throws Exception {
        when(preferenceService.getPreferences(userId, tenantId))
                .thenReturn(preferenceResponse);

        mockMvc.perform(get("/api/v1/notification/preferences/{userId}", userId)
                .header("X-Tenant-ID", tenantId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(preferenceService, times(1)).getPreferences(userId, tenantId);
    }

    @Test
    @DisplayName("Should update user preferences")
    void testUpdatePreferences() throws Exception {
        UserPreferenceRequest request = UserPreferenceRequest.builder()
                .emailEnabled(false)
                .smsEnabled(true)
                .pushEnabled(true)
                .inAppEnabled(false)
                .frequency("DAILY")
                .build();

        when(preferenceService.updatePreferences(userId, tenantId, request))
                .thenReturn(preferenceResponse);

        mockMvc.perform(put("/api/v1/notification/preferences/{userId}", userId)
                .header("X-Tenant-ID", tenantId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(preferenceService, times(1)).updatePreferences(userId, tenantId, request);
    }

    @Test
    @DisplayName("Should check channel enabled status")
    void testIsChannelEnabled() throws Exception {
        when(preferenceService.isChannelEnabled(userId, tenantId, "EMAIL"))
                .thenReturn(true);

        mockMvc.perform(get("/api/v1/notification/preferences/{userId}/channel/EMAIL", userId)
                .header("X-Tenant-ID", tenantId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(preferenceService, times(1)).isChannelEnabled(userId, tenantId, "EMAIL");
    }
}
