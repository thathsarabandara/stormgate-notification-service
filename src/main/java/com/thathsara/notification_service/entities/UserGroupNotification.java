package com.thathsara.notification_service.entities;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing a user group notification.
 */
@Entity
@Table(name = "user_groups")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserGroupNotification {

    /**
     * Unique identifier for the user group notification.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Tenant identifier to which this notification belongs.
     */
    @Column(nullable = false)
    private Long tenantId;

    /**
     * User identifier associated with this notification (nullable).
     */
    @Column(nullable = true)
    private Long userId;

    /**
     * Group associated with this notification.
     */
    @ManyToOne
    @JoinColumn(name = "group_id", referencedColumnName = "id")
    private Group group;

    /**
     * Flag to indicate if the notification has been read.
     */
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    @Builder.Default
    private Boolean isRead = false;

    /**
     * Linked notification entity.
     */
    @OneToOne
    @JoinColumn(name = "notification_id", referencedColumnName = "id")
    private Notification notification;

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
}
