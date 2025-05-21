package com.thathsara.notification_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.thathsara.notification_service.entities.GroupNotification;

public interface  GroupNotificationRepository extends JpaRepository<GroupNotification, Long> {
    
}
