package com.thathsara.notification_service.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for notification list response.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationListResponse {

    /**
     * Notification log ID.
     */
    private UUID id;

    /**
     * Tenant ID.
     */
    @JsonProperty("tenant_id")
    private UUID tenantId;

    /**
     * User ID.
     */
    @JsonProperty("user_id")
    private UUID userId;

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
     * Delivery status.
     */
    private String status;

    /**
     * Subject of the notification.
     */
    private String subject;

    /**
     * Content of the notification.
     */
    private String content;

    /**
     * Timestamp when notification was read.
     */
    @JsonProperty("read_at")
    private LocalDateTime readAt;

    /**
     * Timestamp when notification was sent.
     */
    @JsonProperty("sent_at")
    private LocalDateTime sentAt;

    /**
     * Timestamp when notification was created.
     */
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}
