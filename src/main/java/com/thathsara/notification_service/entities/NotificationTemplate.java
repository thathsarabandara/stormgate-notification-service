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
 * Entity representing a notification template for multi-tenant system.
 * Allows tenants to define custom templates for different notification types and channels.
 */
@Entity
@Table(name = "notification_templates")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationTemplate {

    /**
     * Unique identifier for the template.
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
     * Type of event this template is for (USER_REGISTERED, ORDER_CREATED, etc).
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EventType eventType;

    /**
     * Channel through which notification is sent.
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationChannel channel;

    /**
     * Subject of the notification (mainly for email).
     */
    @Column(nullable = false)
    private String subject;

    /**
     * Body/template content with placeholders (e.g., {{userName}}).
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String body;

    /**
     * Whether this template is enabled.
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean isEnabled = true;

    /**
     * Timestamp when the template was created.
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the template was last updated.
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Enum for different types of events.
     */
    public enum EventType {
        /** User registered event. */
        USER_REGISTERED,
        /** Order created event. */
        ORDER_CREATED,
        /** Order paid event. */
        ORDER_PAID,
        /** Password reset event. */
        PASSWORD_RESET,
        /** Payment failed event. */
        PAYMENT_FAILED,
        /** Order shipped event. */
        ORDER_SHIPPED,
        /** Order delivered event. */
        ORDER_DELIVERED,
        /** Refund initiated event. */
        REFUND_INITIATED,
        /** Refund completed event. */
        REFUND_COMPLETED
    }

    /**
     * Enum for notification delivery channels.
     */
    public enum NotificationChannel {
        /** Email notification channel. */
        EMAIL,
        /** SMS notification channel. */
        SMS,
        /** Push notification channel. */
        PUSH,
        /** In-app notification channel. */
        IN_APP
    }
}
