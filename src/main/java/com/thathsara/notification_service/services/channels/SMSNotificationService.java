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
 * Service for sending SMS notifications via Twilio or similar provider.
 * Currently implemented as a stub for demonstration.
 */
@Slf4j
@Service
public class SMSNotificationService {

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
     * Send SMS notification.
     *
     * @param event The notification event
     * @param template The SMS template
     */
    public void sendSMS(NotificationEventDTO event, NotificationTemplate template) {
        try {
            if (event.getUserPhone() == null || event.getUserPhone().isEmpty()) {
                log.warn("No phone number provided for user: {}", event.getUserId());
                createFailedLog(event, template, "No phone number provided");
                return;
            }

            // Resolve template
            final String resolvedBody = channelService.resolveTemplate(template.getBody(), event.getPayload());

            // TODO: Integrate with Twilio or similar SMS provider
            // TwilioClient.sendSMS(event.getUserPhone(), resolvedBody);

            log.info("SMS sent successfully to: {} for event: {}", event.getUserPhone(), event.getEventType());

            // Log successful delivery
            createSuccessLog(event, template, event.getUserPhone(), resolvedBody);
        } catch (Exception e) {
            log.error("Failed to send SMS to: {}", event.getUserPhone(), e);
            createFailedLog(event, template, e.getMessage());
            throw new RuntimeException("Failed to send SMS notification", e);
        }
    }

    /**
     * Create success log entry.
     *
     * @param event The notification event
     * @param template The template
     * @param recipient The phone number
     * @param content The SMS content
     */
    private void createSuccessLog(NotificationEventDTO event, NotificationTemplate template,
                                   String recipient, String content) {
        NotificationLog log = NotificationLog.builder()
                .tenantId(event.getTenantId())
                .userId(event.getUserId())
                .eventType(NotificationTemplate.EventType.valueOf(event.getEventType()))
                .channel(template.getChannel())
                .status(NotificationLog.DeliveryStatus.SENT)
                .recipient(recipient)
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
        NotificationLog log = NotificationLog.builder()
                .tenantId(event.getTenantId())
                .userId(event.getUserId())
                .eventType(NotificationTemplate.EventType.valueOf(event.getEventType()))
                .channel(template.getChannel())
                .status(NotificationLog.DeliveryStatus.FAILED)
                .recipient(event.getUserPhone())
                .errorMessage(errorMessage)
                .retryCount(0)
                .maxRetries(3)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        notificationLogRepository.save(log);
    }
}
