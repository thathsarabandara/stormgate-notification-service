package com.thathsara.notification_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thathsara.notification_service.entities.UserNotification;

@Repository
public interface UserNotificationRepository extends JpaRepository<UserNotification, Long> {
    
}
