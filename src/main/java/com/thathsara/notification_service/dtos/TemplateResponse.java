package com.thathsara.notification_service.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for template response.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemplateResponse {

    /**
     * Template ID.
     */
    private UUID id;

    /**
     * Tenant ID.
     */
    @JsonProperty("tenant_id")
    private UUID tenantId;

    /**
     * Event type.
     */
    @JsonProperty("event_type")
    private String eventType;

    /**
     * Notification channel.
     */
    private String channel;

    /**
     * Subject of the notification.
     */
    private String subject;

    /**
     * Body content.
     */
    private String body;

    /**
     * Whether template is enabled.
     */
    @JsonProperty("is_enabled")
    private Boolean isEnabled;

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
