package com.thathsara.notification_service.entities;

import java.time.LocalDateTime;

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
 * Entity representing a notification that can be sent via various types such as email, SMS, in-app, or push.
 */
@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    /**
     * Unique identifier for the Notification.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Tenant ID to which this notification belongs.
     */
    @Column(nullable = false)
    private Long tenantid;

    /**
     * Title of the notification.
     */
    @Column(nullable = false)
    private String title;

    /**
     * Message body of the notification.
     */
    @Column(columnDefinition = "TEXT")
    private String message;

    /**
     * Type of the notification (EMAIL, SMS, IN_APP, PUSH).
     */
    @Enumerated(EnumType.STRING)
    private NotificationType type;

    /**
     * Flag indicating if the notification has been deleted.
     */
    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    @Builder.Default
    private boolean isDeleted = false;

    /**
     * Timestamp when the notification was created.
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the notification was last updated.
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Enum representing different notification types.
     */
    public enum NotificationType {
        /**
         * Represents an email notification.
         */
        EMAIL,

        /**
         * Represents an SMS notification.
         */
        SMS,

        /**
         * Represents an in-application notification.
         */
        IN_APP,

        /**
         * Represents a push notification.
         */
        PUSH
    }
}
