package com.thathsara.notification_service.services.channels;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.thathsara.notification_service.dtos.NotificationEventDTO;
import com.thathsara.notification_service.entities.NotificationLog;
import com.thathsara.notification_service.entities.NotificationTemplate;
import com.thathsara.notification_service.repositories.NotificationLogRepository;
import com.thathsara.notification_service.services.ChannelNotificationService;

import lombok.extern.slf4j.Slf4j;

/**
 * Service for storing in-app notifications.
 * In-app notifications are stored in the database and retrieved via REST API.
 */
@Slf4j
@Service
public class InAppNotificationService {

    /**
     * Notification log repository.
     */
    @Autowired
    private NotificationLogRepository notificationLogRepository;

    /**
     * Channel notification service for template resolution.
     */
    @Autowired
    private ChannelNotificationService channelService;

    /**
     * Save in-app notification.
     *
     * @param event The notification event
     * @param template The in-app notification template
     */
    public void sendInAppNotification(NotificationEventDTO event, NotificationTemplate template) {
        try {
            // Resolve template
            final String resolvedSubject = channelService.resolveTemplate(template.getSubject(), event.getPayload());
            final String resolvedBody = channelService.resolveTemplate(template.getBody(), event.getPayload());

            // Create in-app notification log (stored in DB)
            final NotificationLog notificationLog = NotificationLog.builder()
                    .tenantId(event.getTenantId())
                    .userId(event.getUserId())
                    .eventType(NotificationTemplate.EventType.valueOf(event.getEventType()))
                    .channel(template.getChannel())
                    .status(NotificationLog.DeliveryStatus.DELIVERED)
                    .recipient(event.getUserId().toString())
                    .subject(resolvedSubject)
                    .content(resolvedBody)
                    .retryCount(0)
                    .maxRetries(0)
                    .sentAt(LocalDateTime.now())
                    .deliveredAt(LocalDateTime.now())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            notificationLogRepository.save(notificationLog);

            log.info("In-app notification created successfully for user: {} in tenant: {}",
                    event.getUserId(), event.getTenantId());
        } catch (Exception e) {
            log.error("Failed to create in-app notification for user: {}", event.getUserId(), e);
            throw new RuntimeException("Failed to create in-app notification", e);
        }
    }
}
