package com.thathsara.notification_service.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing a notification event from the message broker.
 * This is the event that gets published by other microservices.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationEventDTO {

    /**
     * Unique event ID.
     */
    @JsonProperty("event_id")
    private String eventId;

    /**
     * Type of event (USER_REGISTERED, ORDER_CREATED, etc).
     */
    @JsonProperty("event_type")
    private String eventType;

    /**
     * Tenant ID from the source service.
     */
    @JsonProperty("tenant_id")
    private UUID tenantId;

    /**
     * User ID who should receive the notification.
     */
    @JsonProperty("user_id")
    private UUID userId;

    /**
     * Email address of the user.
     */
    @JsonProperty("user_email")
    private String userEmail;

    /**
     * Phone number of the user (for SMS).
     */
    @JsonProperty("user_phone")
    private String userPhone;

    /**
     * External user ID from source system.
     */
    @JsonProperty("external_user_id")
    private String externalUserId;

    /**
     * Custom payload data for template substitution.
     */
    @JsonProperty("payload")
    private Object payload;

    /**
     * Timestamp when the event was created at source.
     */
    @JsonProperty("timestamp")
    private LocalDateTime timestamp;

    /**
     * Priority level: LOW, MEDIUM, HIGH, CRITICAL.
     */
    @JsonProperty("priority")
    @Builder.Default
    private String priority = "MEDIUM";

    /**
     * Source service that triggered this event.
     */
    @JsonProperty("source_service")
    private String sourceService;
}
