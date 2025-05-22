package com.thathsara.notification_service.repositories;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.thathsara.notification_service.entities.Notification;
import com.thathsara.notification_service.entities.UserNotification;

@Repository
public interface UserNotificationRepository extends JpaRepository<UserNotification, Long> {
    @Query("SELECT n FROM UserNotification n WHERE n.userId = :userId AND n.createdAt >= :threeMonthsAgo")
    Page<UserNotification> findAllByUserId(@Param("userId") Long userId, @Param("threeMonthsAgo") LocalDateTime threeMonthsAgo,Pageable pageable);

    Optional<UserNotification> findByNotificationAndUserId(Notification notification, Long userId);
}
