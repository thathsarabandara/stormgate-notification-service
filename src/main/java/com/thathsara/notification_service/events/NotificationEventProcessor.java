package com.thathsara.notification_service.events;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import com.thathsara.notification_service.dtos.NotificationEventDTO;
import com.thathsara.notification_service.entities.NotificationTemplate;
import com.thathsara.notification_service.entities.UserPreference;
import com.thathsara.notification_service.repositories.NotificationTemplateRepository;
import com.thathsara.notification_service.repositories.UserPreferenceRepository;
import com.thathsara.notification_service.services.ChannelNotificationService;
import com.thathsara.notification_service.services.MailService;

import lombok.extern.slf4j.Slf4j;

/**
 * Event processor for handling notification events from message broker.
 * Routes events to appropriate notification channels based on templates and user preferences.
 */
@Slf4j
@Service
public class NotificationEventProcessor {

    /**
     * Template repository.
     */
    @Autowired
    private NotificationTemplateRepository templateRepository;

    /**
     * User preference repository.
     */
    @Autowired
    private UserPreferenceRepository preferenceRepository;

    /**
     * Channel notification service.
     */
    @Autowired
    private ChannelNotificationService channelService;

    /**
     * Process notification event with retry mechanism.
     *
     * @param event The notification event to process
     */
    @Retryable(
            retryFor = {Exception.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000, multiplier = 2.0)
    )
    public void processEvent(NotificationEventDTO event) {
        log.info("Processing event: {} for user: {} in tenant: {}",
            event.getEventType(), event.getUserId(), event.getTenantId());

        try {
            // Get user preferences
            Optional<UserPreference> preferences = preferenceRepository
                    .findByUserIdAndTenantId(event.getUserId(), event.getTenantId());

            // Find enabled templates for this event
            List<NotificationTemplate> templates = templateRepository
                    .findByTenantIdAndEventTypeAndIsEnabledTrue(
                            event.getTenantId(),
                            NotificationTemplate.EventType.valueOf(event.getEventType())
                    );

            if (templates.isEmpty()) {
                log.warn("No enabled templates found for event type: {} in tenant: {}",
                        event.getEventType(), event.getTenantId());
                return;
            }

            // Process each template channel
            for (NotificationTemplate template : templates) {
                if (shouldSendNotification(template, preferences)) {
                    channelService.sendNotification(event, template);
                }
            }

            log.info("Successfully processed event: {}", event.getEventId());
        } catch (Exception e) {
            log.error("Error processing notification event: {}", event.getEventId(), e);
            throw new RuntimeException("Failed to process notification event", e);
        }
    }

    /**
     * Determine if notification should be sent based on template and user preferences.
     *
     * @param template The notification template
     * @param preferences The user preferences
     * @return true if notification should be sent
     */
    private boolean shouldSendNotification(NotificationTemplate template, Optional<UserPreference> preferences) {
        if (preferences.isEmpty()) {
            return true; // Send if no preferences set
        }

        UserPreference pref = preferences.get();

        return switch (template.getChannel()) {
            case EMAIL -> pref.getEmailEnabled();
            case SMS -> pref.getSmsEnabled();
            case PUSH -> pref.getPushEnabled();
            case IN_APP -> pref.getInAppEnabled();
        };
    }
}
