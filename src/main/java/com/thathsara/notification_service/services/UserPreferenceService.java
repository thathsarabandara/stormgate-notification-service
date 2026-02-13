package com.thathsara.notification_service.services;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thathsara.notification_service.dtos.UserPreferenceRequest;
import com.thathsara.notification_service.dtos.UserPreferenceResponse;
import com.thathsara.notification_service.entities.UserPreference;
import com.thathsara.notification_service.repositories.UserPreferenceRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * Service for managing user notification preferences.
 */
@Slf4j
@Service
public class UserPreferenceService {

    /**
     * User preference repository.
     */
    @Autowired
    private UserPreferenceRepository preferenceRepository;

    /**
     * Get user preferences.
     *
     * @param userId The user ID
     * @param tenantId The tenant ID
     * @return User preference response
     */
    public UserPreferenceResponse getPreferences(UUID userId, UUID tenantId) {
        Optional<UserPreference> preference = preferenceRepository.findByUserIdAndTenantId(userId, tenantId);

        return preference.map(this::mapToResponse).orElseGet(() ->
                createDefaultPreferences(userId, tenantId)
        );
    }

    /**
     * Update user preferences.
     *
     * @param userId The user ID
     * @param tenantId The tenant ID
     * @param request The preference update request
     * @return Updated preference response
     */
    @Transactional
    public UserPreferenceResponse updatePreferences(UUID userId, UUID tenantId, UserPreferenceRequest request) {
        log.info("Updating preferences for user: {} in tenant: {}", userId, tenantId);

        Optional<UserPreference> existing = preferenceRepository.findByUserIdAndTenantId(userId, tenantId);

        UserPreference preference;
        if (existing.isPresent()) {
            preference = existing.get();
            preference.setEmailEnabled(request.getEmailEnabled());
            preference.setSmsEnabled(request.getSmsEnabled());
            preference.setPushEnabled(request.getPushEnabled());
            preference.setInAppEnabled(request.getInAppEnabled());
            preference.setFrequency(request.getFrequency());
        } else {
            preference = UserPreference.builder()
                    .userId(userId)
                    .tenantId(tenantId)
                    .emailEnabled(request.getEmailEnabled())
                    .smsEnabled(request.getSmsEnabled())
                    .pushEnabled(request.getPushEnabled())
                    .inAppEnabled(request.getInAppEnabled())
                    .frequency(request.getFrequency())
                    .build();
        }

        UserPreference saved = preferenceRepository.save(preference);
        log.info("Preferences updated successfully for user: {}", userId);

        return mapToResponse(saved);
    }

    /**
     * Check if a specific channel is enabled for a user.
     *
     * @param userId The user ID
     * @param tenantId The tenant ID
     * @param channel The notification channel
     * @return true if channel is enabled
     */
    public boolean isChannelEnabled(UUID userId, UUID tenantId, String channel) {
        Optional<UserPreference> preference = preferenceRepository.findByUserIdAndTenantId(userId, tenantId);

        if (preference.isEmpty()) {
            return true; // Default to enabled if no preferences set
        }

        UserPreference pref = preference.get();
        return switch (channel.toUpperCase()) {
            case "EMAIL" -> pref.getEmailEnabled();
            case "SMS" -> pref.getSmsEnabled();
            case "PUSH" -> pref.getPushEnabled();
            case "IN_APP" -> pref.getInAppEnabled();
            default -> false;
        };
    }

    /**
     * Create default preferences for a new user.
     *
     * @param userId The user ID
     * @param tenantId The tenant ID
     * @return Default preference response
     */
    @Transactional
    private UserPreferenceResponse createDefaultPreferences(UUID userId, UUID tenantId) {
        log.info("Creating default preferences for user: {} in tenant: {}", userId, tenantId);

        UserPreference preference = UserPreference.builder()
                .userId(userId)
                .tenantId(tenantId)
                .emailEnabled(true)
                .smsEnabled(false)
                .pushEnabled(true)
                .inAppEnabled(true)
                .frequency("IMMEDIATE")
                .build();

        UserPreference saved = preferenceRepository.save(preference);
        return mapToResponse(saved);
    }

    /**
     * Map entity to DTO response.
     *
     * @param preference The preference entity
     * @return The preference response DTO
     */
    private UserPreferenceResponse mapToResponse(UserPreference preference) {
        return UserPreferenceResponse.builder()
                .id(preference.getId())
                .userId(preference.getUserId())
                .tenantId(preference.getTenantId())
                .emailEnabled(preference.getEmailEnabled())
                .smsEnabled(preference.getSmsEnabled())
                .pushEnabled(preference.getPushEnabled())
                .inAppEnabled(preference.getInAppEnabled())
                .frequency(preference.getFrequency())
                .createdAt(preference.getCreatedAt())
                .updatedAt(preference.getUpdatedAt())
                .build();
    }
}
