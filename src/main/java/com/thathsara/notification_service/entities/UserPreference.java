package com.thathsara.notification_service.entities;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing user notification preferences.
 * Allows users to control which notification channels they want to receive.
 */
@Entity
@Table(name = "user_preferences")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPreference {

    /**
     * Unique identifier for the preference.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * User ID.
     */
    @Column(nullable = false)
    private UUID userId;

    /**
     * Tenant ID for multi-tenant isolation.
     */
    @Column(nullable = false)
    private UUID tenantId;

    /**
     * Whether user wants to receive email notifications.
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean emailEnabled = true;

    /**
     * Whether user wants to receive SMS notifications.
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean smsEnabled = false;

    /**
     * Whether user wants to receive push notifications.
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean pushEnabled = true;

    /**
     * Whether user wants to receive in-app notifications.
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean inAppEnabled = true;

    /**
     * Notification frequency: IMMEDIATE, DAILY, WEEKLY, NEVER.
     */
    @Column(nullable = false)
    @Builder.Default
    private String frequency = "IMMEDIATE";

    /**
     * Timestamp when the preference was created.
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the preference was last updated.
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
