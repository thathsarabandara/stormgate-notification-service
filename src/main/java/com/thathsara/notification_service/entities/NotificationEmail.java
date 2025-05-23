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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing a notification email linked to a user notification.
 */
@Entity
@Table(name = "notification_emails")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationEmail {

    /**
     * Unique identifier for the NotificationEmail.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Associated user notification entity.
     */
    @OneToOne
    @JoinColumn(name = "notification_id", referencedColumnName = "id")
    private UserNotification userNotification;

    /**
     * Recipient's email address.
     */
    @Column(name = "recipient_email", nullable = false)
    private String recipientEmail;

    /**
     * Timestamp when the notification email was created.
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the notification email was last updated.
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
