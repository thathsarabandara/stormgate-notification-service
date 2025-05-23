package com.thathsara.notification_service.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.thathsara.notification_service.entities.Group;
import com.thathsara.notification_service.entities.Notification;
import com.thathsara.notification_service.entities.UserGroupNotification;

public interface UserGroupNotificationRepository extends JpaRepository<UserGroupNotification, Long> {
    Optional<UserGroupNotification> findByNotificationAndUserIdAndGroup(
        Notification notification, 
        Long userId, 
        Group group
    );
}
