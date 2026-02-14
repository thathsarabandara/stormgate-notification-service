package com.thathsara.notification_service.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.thathsara.notification_service.dtos.UserPreferenceRequest;
import com.thathsara.notification_service.dtos.UserPreferenceResponse;
import com.thathsara.notification_service.entities.UserPreference;
import com.thathsara.notification_service.repositories.UserPreferenceRepository;

/**
 * Unit tests for UserPreferenceService.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserPreferenceService Tests")
class UserPreferenceServiceTests {

    @Mock
    private UserPreferenceRepository preferenceRepository;

    @InjectMocks
    private UserPreferenceService preferenceService;

    private UUID userId;
    private UUID tenantId;
    private UserPreference preference;
    private UserPreferenceRequest request;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        tenantId = UUID.randomUUID();

        preference = UserPreference.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .tenantId(tenantId)
                .emailEnabled(true)
                .smsEnabled(true)
                .pushEnabled(false)
                .inAppEnabled(true)
                .frequency("IMMEDIATE")
                .build();

        request = UserPreferenceRequest.builder()
                .emailEnabled(true)
                .smsEnabled(true)
                .pushEnabled(false)
                .inAppEnabled(true)
                .frequency("IMMEDIATE")
                .build();
    }

    @Test
    @DisplayName("Should get existing user preferences")
    void testGetExistingPreferences() {
        when(preferenceRepository.findByUserIdAndTenantId(userId, tenantId))
                .thenReturn(Optional.of(preference));

        UserPreferenceResponse response = preferenceService.getPreferences(userId, tenantId);

        assertNotNull(response);
        assertTrue(response.getEmailEnabled());
        assertTrue(response.getSmsEnabled());
        assertFalse(response.getPushEnabled());
        verify(preferenceRepository, times(1)).findByUserIdAndTenantId(userId, tenantId);
    }

    @Test
    @DisplayName("Should create default preferences when not found")
    void testGetDefaultPreferences() {
        when(preferenceRepository.findByUserIdAndTenantId(userId, tenantId))
                .thenReturn(Optional.empty());
        when(preferenceRepository.save(any(UserPreference.class)))
                .thenReturn(preference);

        UserPreferenceResponse response = preferenceService.getPreferences(userId, tenantId);

        assertNotNull(response);
        verify(preferenceRepository, times(1)).findByUserIdAndTenantId(userId, tenantId);
        verify(preferenceRepository, times(1)).save(any(UserPreference.class));
    }

    @Test
    @DisplayName("Should update existing preferences")
    void testUpdateExistingPreferences() {
        when(preferenceRepository.findByUserIdAndTenantId(userId, tenantId))
                .thenReturn(Optional.of(preference));
        when(preferenceRepository.save(any(UserPreference.class)))
                .thenReturn(preference);

        UserPreferenceRequest updateRequest = UserPreferenceRequest.builder()
                .emailEnabled(false)
                .smsEnabled(true)
                .pushEnabled(true)
                .inAppEnabled(false)
                .frequency("WEEKLY")
                .build();

        UserPreferenceResponse response = preferenceService.updatePreferences(userId, tenantId, updateRequest);

        assertNotNull(response);
        verify(preferenceRepository, times(1)).save(any(UserPreference.class));
    }

    @Test
    @DisplayName("Should create new preferences if not exist during update")
    void testCreateNewPreferencesOnUpdate() {
        when(preferenceRepository.findByUserIdAndTenantId(userId, tenantId))
                .thenReturn(Optional.empty());
        when(preferenceRepository.save(any(UserPreference.class)))
                .thenReturn(preference);

        UserPreferenceResponse response = preferenceService.updatePreferences(userId, tenantId, request);

        assertNotNull(response);
        verify(preferenceRepository, times(1)).save(any(UserPreference.class));
    }

    @Test
    @DisplayName("Should check if email channel is enabled")
    void testIsEmailChannelEnabled() {
        when(preferenceRepository.findByUserIdAndTenantId(userId, tenantId))
                .thenReturn(Optional.of(preference));

        boolean enabled = preferenceService.isChannelEnabled(userId, tenantId, "EMAIL");

        assertTrue(enabled);
    }

    @Test
    @DisplayName("Should check if SMS channel is enabled")
    void testIsSmsTChannelEnabled() {
        when(preferenceRepository.findByUserIdAndTenantId(userId, tenantId))
                .thenReturn(Optional.of(preference));

        boolean enabled = preferenceService.isChannelEnabled(userId, tenantId, "SMS");

        assertTrue(enabled);
    }

    @Test
    @DisplayName("Should check if push channel is disabled")
    void testIsPushChannelDisabled() {
        when(preferenceRepository.findByUserIdAndTenantId(userId, tenantId))
                .thenReturn(Optional.of(preference));

        boolean enabled = preferenceService.isChannelEnabled(userId, tenantId, "PUSH");

        assertFalse(enabled);
    }

    @Test
    @DisplayName("Should default to enabled when preferences not found")
    void testChannelDefaultToEnabledWhenNotFound() {
        when(preferenceRepository.findByUserIdAndTenantId(userId, tenantId))
                .thenReturn(Optional.empty());

        boolean enabled = preferenceService.isChannelEnabled(userId, tenantId, "EMAIL");

        assertTrue(enabled);
    }

    @Test
    @DisplayName("Should check all channel types")
    void testAllChannelTypes() {
        when(preferenceRepository.findByUserIdAndTenantId(userId, tenantId))
                .thenReturn(Optional.of(preference));

        assertTrue(preferenceService.isChannelEnabled(userId, tenantId, "EMAIL"));
        assertTrue(preferenceService.isChannelEnabled(userId, tenantId, "SMS"));
        assertFalse(preferenceService.isChannelEnabled(userId, tenantId, "PUSH"));
        assertTrue(preferenceService.isChannelEnabled(userId, tenantId, "IN_APP"));
    }

    @Test
    @DisplayName("Should check frequency setting")
    void testFrequencySetting() {
        when(preferenceRepository.findByUserIdAndTenantId(userId, tenantId))
                .thenReturn(Optional.of(preference));

        UserPreferenceResponse response = preferenceService.getPreferences(userId, tenantId);

        assertNotNull(response);
        assertEquals("IMMEDIATE", response.getFrequency());
    }
}
