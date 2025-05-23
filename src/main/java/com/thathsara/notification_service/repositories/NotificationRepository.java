package com.thathsara.notification_service.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thathsara.notification_service.entities.Notification;

@Repository
public interface NotificationRepository extends  JpaRepository<Notification, Long> {
    Optional<Notification> findByIdAndIsDeleted(Long id, boolean isDeleted);
    Page<Notification> findByTenantid(Long tenantid, Pageable pageable);
}
