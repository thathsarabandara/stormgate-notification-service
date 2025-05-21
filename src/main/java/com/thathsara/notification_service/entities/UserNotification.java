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
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "notification_id", referencedColumnName="id")
    private Notification notification;

    @Column(nullable=false)
    private Long userId;

    @Column(nullable=false)
    private Boolean isRead;

    @Column(nullable=false)
    private LocalDateTime sentAt;

    @CreationTimestamp
    @Column(nullable=false, name="created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable=false, name="created_at")
    private LocalDateTime updatedAt;
}