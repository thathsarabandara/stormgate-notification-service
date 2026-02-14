package com.thathsara.notification_service.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user preference request.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPreferenceRequest {

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
}
