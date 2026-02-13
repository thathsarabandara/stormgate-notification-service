package com.thathsara.notification_service.services.channels;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.thathsara.notification_service.dtos.NotificationEventDTO;
import com.thathsara.notification_service.entities.NotificationLog;
import com.thathsara.notification_service.entities.NotificationTemplate;
import com.thathsara.notification_service.repositories.NotificationLogRepository;
import com.thathsara.notification_service.services.ChannelNotificationService;

import lombok.extern.slf4j.Slf4j;

/**
 * Service for sending push notifications via Firebase Cloud Messaging (FCM).
 */
@Slf4j
@Service
public class PushNotificationService {

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
     * Firebase messaging instance (optional).
     */
    @Autowired(required = false)
    private FirebaseMessaging firebaseMessaging;

    /**
     * Send push notification via FCM.
     *
     * @param event The notification event
     * @param template The push notification template
     */
    public void sendPushNotification(NotificationEventDTO event, NotificationTemplate template) {
        try {
            if (firebaseMessaging == null) {
                log.warn("Firebase Messaging not configured. Skipping push notification.");
                createSkippedLog(event, template, "Firebase not configured");
                return;
            }

            // Resolve template
            final String resolvedSubject = channelService.resolveTemplate(template.getSubject(), event.getPayload());
            final String resolvedBody = channelService.resolveTemplate(template.getBody(), event.getPayload());

            // Build Firebase message
            final Message message = Message.builder()
                    .setNotification(Notification.builder()
                            .setTitle(resolvedSubject)
                            .setBody(resolvedBody)
                            .build())
                    .setToken("device-token-here") // Replace with actual device token
                    .build();

            // Send message
            final String response = firebaseMessaging.send(message);
            log.info("Push notification sent successfully. Message ID: {}", response);

            // Log successful delivery
            createSuccessLog(event, template, "device-token", resolvedSubject, resolvedBody);
        } catch (Exception e) {
            log.error("Failed to send push notification for user: {}", event.getUserId(), e);
            createFailedLog(event, template, e.getMessage());
            throw new RuntimeException("Failed to send push notification", e);
        }
    }

    /**
     * Create success log entry.
     *
     * @param event The notification event
     * @param template The template
     * @param deviceToken The device token
     * @param subject The notification title
     * @param content The notification content
     */
    private void createSuccessLog(NotificationEventDTO event, NotificationTemplate template,
                                   String deviceToken, String subject, String content) {
        final NotificationLog log = NotificationLog.builder()
                .tenantId(event.getTenantId())
                .userId(event.getUserId())
                .eventType(NotificationTemplate.EventType.valueOf(event.getEventType()))
                .channel(template.getChannel())
                .status(NotificationLog.DeliveryStatus.SENT)
                .recipient(deviceToken)
                .subject(subject)
                .content(content)
                .retryCount(0)
                .maxRetries(3)
                .sentAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        notificationLogRepository.save(log);
    }

    /**
     * Create failed log entry.
     *
     * @param event The notification event
     * @param template The template
     * @param errorMessage The error message
     */
    private void createFailedLog(NotificationEventDTO event, NotificationTemplate template, String errorMessage) {
        final NotificationLog log = NotificationLog.builder()
                .tenantId(event.getTenantId())
                .userId(event.getUserId())
                .eventType(NotificationTemplate.EventType.valueOf(event.getEventType()))
                .channel(template.getChannel())
                .status(NotificationLog.DeliveryStatus.FAILED)
                .recipient("unknown")
                .errorMessage(errorMessage)
                .retryCount(0)
                .maxRetries(3)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        notificationLogRepository.save(log);
    }

    /**
     * Create skipped log entry.
     *
     * @param event The notification event
     * @param template The template
     * @param reason The reason for skipping
     */
    private void createSkippedLog(NotificationEventDTO event, NotificationTemplate template, String reason) {
        final NotificationLog log = NotificationLog.builder()
                .tenantId(event.getTenantId())
                .userId(event.getUserId())
                .eventType(NotificationTemplate.EventType.valueOf(event.getEventType()))
                .channel(template.getChannel())
                .status(NotificationLog.DeliveryStatus.FAILED)
                .recipient("unknown")
                .errorMessage(reason)
                .retryCount(0)
                .maxRetries(3)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        notificationLogRepository.save(log);
    }
}
