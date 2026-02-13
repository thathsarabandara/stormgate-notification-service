package com.thathsara.notification_service.services.channels;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.thathsara.notification_service.dtos.NotificationEventDTO;
import com.thathsara.notification_service.entities.NotificationLog;
import com.thathsara.notification_service.entities.NotificationTemplate;
import com.thathsara.notification_service.repositories.NotificationLogRepository;
import com.thathsara.notification_service.services.ChannelNotificationService;

import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

/**
 * Service for sending email notifications.
 */
@Slf4j
@Service
public class EmailNotificationService {

    /**
     * Java mail sender.
     */
    @Autowired
    private JavaMailSender mailSender;

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
     * Send email notification.
     *
     * @param event The notification event
     * @param template The email template
     */
    public void sendEmail(NotificationEventDTO event, NotificationTemplate template) {
        try {
            if (event.getUserEmail() == null || event.getUserEmail().isEmpty()) {
                log.warn("No email address provided for user: {}", event.getUserId());
                createFailedLog(event, template, "No email address provided");
                return;
            }

            // Resolve template placeholders
            final String resolvedSubject = channelService.resolveTemplate(template.getSubject(), event.getPayload());
            final String resolvedBody = channelService.resolveTemplate(template.getBody(), event.getPayload());

            // Send email
            final MimeMessage message = mailSender.createMimeMessage();
            final MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(event.getUserEmail());
            helper.setSubject(resolvedSubject);
            helper.setText(resolvedBody, true);
            helper.setFrom("noreply@stormgate.com");

            mailSender.send(message);

            log.info("Email sent successfully to: {} for event: {}", event.getUserEmail(), event.getEventType());

            // Log successful delivery
            createSuccessLog(event, template, event.getUserEmail(), resolvedSubject, resolvedBody);
        } catch (Exception e) {
            log.error("Failed to send email to: {}", event.getUserEmail(), e);
            createFailedLog(event, template, e.getMessage());
            throw new RuntimeException("Failed to send email notification", e);
        }
    }

    /**
     * Create success log entry.
     *
     * @param event The notification event
     * @param template The template
     * @param recipient The recipient email
     * @param subject The email subject
     * @param content The email content
     */
    private void createSuccessLog(NotificationEventDTO event, NotificationTemplate template,
                                   String recipient, String subject, String content) {
        final NotificationLog log = NotificationLog.builder()
                .tenantId(event.getTenantId())
                .userId(event.getUserId())
                .eventType(NotificationTemplate.EventType.valueOf(event.getEventType()))
                .channel(template.getChannel())
                .status(NotificationLog.DeliveryStatus.SENT)
                .recipient(recipient)
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
                .recipient(event.getUserEmail())
                .errorMessage(errorMessage)
                .retryCount(0)
                .maxRetries(3)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        notificationLogRepository.save(log);
    }
}
