package com.thathsara.notification_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.thathsara.notification_service.entities.NotificationEmail;

public interface NotificationEmailRepository extends JpaRepository<NotificationEmail, Long> {
    
}
