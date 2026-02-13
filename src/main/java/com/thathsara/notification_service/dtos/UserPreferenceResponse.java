package com.thathsara.notification_service.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user preference response.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPreferenceResponse {

    /**
     * Preference ID.
     */
    private UUID id;

    /**
     * User ID.
     */
    @JsonProperty("user_id")
    private UUID userId;

    /**
     * Tenant ID.
     */
    @JsonProperty("tenant_id")
    private UUID tenantId;

    /**
     * Email enabled.
     */
    @JsonProperty("email_enabled")
    private Boolean emailEnabled;

    /**
     * SMS enabled.
     */
    @JsonProperty("sms_enabled")
    private Boolean smsEnabled;

    /**
     * Push enabled.
     */
    @JsonProperty("push_enabled")
    private Boolean pushEnabled;

    /**
     * In-app enabled.
     */
    @JsonProperty("in_app_enabled")
    private Boolean inAppEnabled;

    /**
     * Notification frequency.
     */
    private String frequency;

    /**
     * Created timestamp.
     */
    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    /**
     * Updated timestamp.
     */
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
}
