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

@Entity
@Table(name = "user_groups")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserGroupNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private Long tenantId;

    @Column(nullable=true)
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "group_id", referencedColumnName="id")
    private Group group;

    @Column(nullable=false, columnDefinition="BOOLEAN DEFAULT FALSE")
    @Builder.Default
    private Boolean isRead = false;

    @OneToOne
    @JoinColumn(name = "notifictaion_id", referencedColumnName = "id")
    private Notification notification;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
