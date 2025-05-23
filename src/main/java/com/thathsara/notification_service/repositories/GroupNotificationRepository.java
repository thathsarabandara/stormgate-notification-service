package com.thathsara.notification_service.repositories;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.thathsara.notification_service.entities.Group;
import com.thathsara.notification_service.entities.GroupNotification;
import com.thathsara.notification_service.entities.Notification;

public interface  GroupNotificationRepository extends JpaRepository<GroupNotification, Long> {
    @Query("SELECT n FROM GroupNotification n "
     + "WHERE n.group = :group "
     + "AND n.createdAt >= :threeMonthsAgo")
    Page<GroupNotification> findAllByGroup(
        @Param("group") Group group, 
        @Param("threeMonthsAgo") LocalDateTime threeMonthsAgo, 
        Pageable pageable);
        
    Optional<GroupNotification> findByNotification(Notification notification);
}
