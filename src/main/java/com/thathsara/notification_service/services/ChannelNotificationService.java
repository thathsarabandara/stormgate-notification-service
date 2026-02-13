package com.thathsara.notification_service.services;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.thathsara.notification_service.dtos.NotificationEventDTO;
import com.thathsara.notification_service.entities.NotificationLog;
import com.thathsara.notification_service.entities.NotificationTemplate;
import com.thathsara.notification_service.repositories.NotificationLogRepository;
import com.thathsara.notification_service.services.channels.EmailNotificationService;
import com.thathsara.notification_service.services.channels.InAppNotificationService;
import com.thathsara.notification_service.services.channels.PushNotificationService;
import com.thathsara.notification_service.services.channels.SMSNotificationService;

import lombok.extern.slf4j.Slf4j;

/**
 * Service to route notifications to appropriate channels based on template configuration.
 */
@Slf4j
@Service
public class ChannelNotificationService {

    /**
     * Email notification service.
     */
    @Autowired
    private EmailNotificationService emailService;

    /**
     * SMS notification service.
     */
    @Autowired
    private SMSNotificationService smsService;

    /**
     * Push notification service.
     */
    @Autowired
    private PushNotificationService pushService;

    /**
     * In-app notification service.
     */
    @Autowired
    private InAppNotificationService inAppService;

    /**
     * Notification log repository for tracking.
     */
    @Autowired
    private NotificationLogRepository notificationLogRepository;

    /**
     * Send notification via appropriate channel.
     *
     * @param event The notification event
     * @param template The notification template
     */
    public void sendNotification(NotificationEventDTO event, NotificationTemplate template) {
        log.info("Sending {} notification for event: {}", template.getChannel(), event.getEventType());

        try {
            switch (template.getChannel()) {
                case EMAIL:
                    emailService.sendEmail(event, template);
                    break;
                case SMS:
                    smsService.sendSMS(event, template);
                    break;
                case PUSH:
                    pushService.sendPushNotification(event, template);
                    break;
                case IN_APP:
                    inAppService.sendInAppNotification(event, template);
                    break;
                default:
                    log.warn("Unknown notification channel: {}", template.getChannel());
            }
        } catch (Exception e) {
            log.error("Error sending {} notification", template.getChannel(), e);
            createFailedLog(event, template, e);
        }
    }

    /**
     * Create a log entry for failed notification delivery.
     *
     * @param event The notification event
     * @param template The notification template
     * @param exception The exception that occurred
     */
    private void createFailedLog(NotificationEventDTO event, NotificationTemplate template, Exception exception) {
        final NotificationLog log = NotificationLog.builder()
                .tenantId(event.getTenantId())
                .userId(event.getUserId())
                .eventType(NotificationTemplate.EventType.valueOf(event.getEventType()))
                .channel(template.getChannel())
                .status(NotificationLog.DeliveryStatus.FAILED)
                .recipient(getRecipient(event, template))
                .subject(template.getSubject())
                .content(template.getBody())
                .errorMessage(exception.getMessage())
                .retryCount(0)
                .maxRetries(3)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        notificationLogRepository.save(log);
    }

    /**
     * Get recipient address based on channel.
     *
     * @param event The notification event
     * @param template The notification template
     * @return The recipient address
     */
    private String getRecipient(NotificationEventDTO event, NotificationTemplate template) {
        return switch (template.getChannel()) {
            case EMAIL -> event.getUserEmail();
            case SMS -> event.getUserPhone();
            case PUSH -> "device-token"; // Will be resolved in push service
            case IN_APP -> event.getUserId().toString();
        };
    }

    /**
     * Resolve template placeholders with event data.
     *
     * @param template The template text with placeholders
     * @param payload The event payload
     * @return Resolved template text
     */
    public String resolveTemplate(String template, Object payload) {
        String result = template;

        if (payload instanceof Map) {
            @SuppressWarnings("unchecked")
            final Map<String, Object> map = (Map<String, Object>) payload;

            for (Map.Entry<String, Object> entry : map.entrySet()) {
                final String placeholder = entry.getKey();
                final String value = entry.getValue() != null ? entry.getValue().toString() : "";
                result = result.replace(placeholder, value);
            }
        }

        return result;
    }
}
