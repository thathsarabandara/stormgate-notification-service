package com.thathsara.notification_service.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.thathsara.notification_service.entities.NotificationLog;
import com.thathsara.notification_service.entities.NotificationLog.DeliveryStatus;
import com.thathsara.notification_service.entities.NotificationTemplate.NotificationChannel;

/**
 * Repository interface for NotificationLog entity.
 * Provides methods to track and query notification delivery status.
 */
@Repository
public interface NotificationLogRepository extends JpaRepository<NotificationLog, UUID> {

    /**
     * Find all logs for a specific user.
     *
     * @param userId The user ID
     * @param tenantId The tenant ID
     * @return List of notification logs
     */
    List<NotificationLog> findByUserIdAndTenantId(UUID userId, UUID tenantId);

    /**
     * Find all failed notifications for retry.
     *
     * @param status The delivery status
     * @param maxRetries The maximum retry count
     * @return List of notification logs
     */
    @Query("SELECT n FROM NotificationLog n WHERE n.status = :status AND n.retryCount < :maxRetries")
    List<NotificationLog> findFailedNotificationsForRetry(
            @Param("status") DeliveryStatus status,
            @Param("maxRetries") Integer maxRetries
    );

    /**
     * Count sent notifications for a specific channel.
     *
     * @param channel The notification channel
     * @param tenantId The tenant ID
     * @param status The delivery status
     * @return Count of sent notifications
     */
    Long countByChannelAndTenantIdAndStatus(
            NotificationChannel channel,
            UUID tenantId,
            DeliveryStatus status
    );

    /**
     * Find logs for a specific channel and status.
     *
     * @param channel The notification channel
     * @param status The delivery status
     * @param tenantId The tenant ID
     * @return List of notification logs
     */
    List<NotificationLog> findByChannelAndStatusAndTenantId(
            NotificationChannel channel,
            DeliveryStatus status,
            UUID tenantId
    );

    /**
     * Find logs within a date range for analytics.
     *
     * @param tenantId The tenant ID
     * @param startDate The start date
     * @param endDate The end date
     * @return List of notification logs
     */
    @Query("SELECT n FROM NotificationLog n WHERE n.tenantId = :tenantId " +
           "AND n.createdAt BETWEEN :startDate AND :endDate")
    List<NotificationLog> findByTenantIdAndDateRange(
            @Param("tenantId") UUID tenantId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}
