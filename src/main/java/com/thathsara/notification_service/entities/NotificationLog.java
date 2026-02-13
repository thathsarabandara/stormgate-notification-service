package com.thathsara.notification_service.entities;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity for tracking notification delivery status and logs.
 * Maintains audit trail of notification attempts and results.
 */
@Entity
@Table(name = "notification_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationLog {

    /**
     * Unique identifier for the log.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Tenant ID for multi-tenant isolation.
     */
    @Column(nullable = false)
    private UUID tenantId;

    /**
     * User ID who received the notification.
     */
    @Column(nullable = false)
    private UUID userId;

    /**
     * Event type that triggered this notification.
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationTemplate.EventType eventType;

    /**
     * Channel through which notification was sent.
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationTemplate.NotificationChannel channel;

    /**
     * Status of the notification delivery.
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private DeliveryStatus status = DeliveryStatus.PENDING;

    /**
     * Recipient address (email address, phone number, or device token).
     */
    @Column(nullable = false)
    private String recipient;

    /**
     * Subject of the notification (mainly for email).
     */
    private String subject;

    /**
     * Content of the notification.
     */
    @Column(columnDefinition = "TEXT")
    private String content;

    /**
     * Error message if delivery failed.
     */
    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    /**
     * Number of retry attempts.
     */
    @Builder.Default
    private Integer retryCount = 0;

    /**
     * Maximum number of retries allowed.
     */
    @Builder.Default
    private Integer maxRetries = 3;

    /**
     * Timestamp when the notification was sent.
     */
    private LocalDateTime sentAt;

    /**
     * Timestamp when the notification was delivered.
     */
    private LocalDateTime deliveredAt;

    /**
     * Timestamp when the notification was read (for in-app).
     */
    private LocalDateTime readAt;

    /**
     * Timestamp when the log was created.
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the log was last updated.
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Enum for delivery status.
     */
    public enum DeliveryStatus {
        /** Notification pending delivery. */
        PENDING,
        /** Notification sent to provider. */
        SENT,
        /** Notification delivered to recipient. */
        DELIVERED,
        /** Notification delivery failed. */
        FAILED,
        /** Email bounced. */
        BOUNCED,
        /** User unsubscribed. */
        UNSUBSCRIBED,
        /** In-app notification read by user. */
        READ
    }
}
