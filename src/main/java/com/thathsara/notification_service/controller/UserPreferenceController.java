package com.thathsara.notification_service.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thathsara.notification_service.dtos.UserPreferenceRequest;
import com.thathsara.notification_service.dtos.UserPreferenceResponse;
import com.thathsara.notification_service.services.UserPreferenceService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller for user notification preferences management.
 */
@Slf4j
@RestController
@RequestMapping("/preferences")
public class UserPreferenceController {

    /**
     * User preference service.
     */
    @Autowired
    private UserPreferenceService preferenceService;

    /**
     * Get current user's notification preferences.
     *
     * @param authentication The authentication object
     * @return User preference response
     */
    @GetMapping
    public ResponseEntity<UserPreferenceResponse> getPreferences(Authentication authentication) {
        log.info("Fetching preferences for user: {}", authentication.getName());

        try {
            // Extract user and tenant from authentication
            final UUID userId = UUID.fromString(authentication.getName());
            final UUID tenantId = (UUID) authentication.getCredentials();

            final UserPreferenceResponse response = preferenceService.getPreferences(userId, tenantId);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching user preferences", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Update current user's notification preferences.
     *
     * @param request The preference update request
     * @param authentication The authentication object
     * @return Updated preference response
     */
    @PutMapping
    public ResponseEntity<UserPreferenceResponse> updatePreferences(
            @Valid @RequestBody UserPreferenceRequest request,
            Authentication authentication) {

        log.info("Updating preferences for user: {}", authentication.getName());

        try {
            // Extract user and tenant from authentication
            final UUID userId = UUID.fromString(authentication.getName());
            final UUID tenantId = (UUID) authentication.getCredentials();

            final UserPreferenceResponse response = preferenceService.updatePreferences(
                    userId, tenantId, request
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error updating user preferences", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
