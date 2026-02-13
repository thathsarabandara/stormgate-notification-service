package com.thathsara.notification_service.events;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.thathsara.notification_service.dtos.NotificationEventDTO;
import com.thathsara.notification_service.events.NotificationEventProcessor;

import lombok.extern.slf4j.Slf4j;

/**
 * Package documentation
 */

/**
 * Kafka consumer for notification events.
 * Listens to various event topics from different microservices and triggers notification processing.
 */
@Slf4j
@Service
public class NotificationEventConsumer {

    /**
     * Event processor service.
     */
    @Autowired
    private NotificationEventProcessor eventProcessor;

    /**
     * Listens to user-related events (registration, password reset, etc.).
     *
     * @param event The notification event from Kafka
     */
    @KafkaListener(
            topics = "${kafka.topics.user-events:user-events}",
            groupId = "${kafka.consumer.group-id:notification-service-group}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleUserEvents(NotificationEventDTO event) {
        log.info("Received user event: {} for tenant: {}", event.getEventType(), event.getTenantId());
        try {
            eventProcessor.processEvent(event);
        } catch (Exception e) {
            log.error("Error processing user event: {}", event.getEventId(), e);
            // Event will be retried based on Kafka configuration
        }
    }

    /**
     * Listens to order-related events (order created, paid, shipped, etc.).
     *
     * @param event The notification event from Kafka
     */
    @KafkaListener(
            topics = "${kafka.topics.order-events:order-events}",
            groupId = "${kafka.consumer.group-id:notification-service-group}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleOrderEvents(NotificationEventDTO event) {
        log.info("Received order event: {} for tenant: {}", event.getEventType(), event.getTenantId());
        try {
            eventProcessor.processEvent(event);
        } catch (Exception e) {
            log.error("Error processing order event: {}", event.getEventId(), e);
        }
    }

    /**
     * Listens to payment-related events.
     *
     * @param event The notification event from Kafka
     */
    @KafkaListener(
            topics = "${kafka.topics.payment-events:payment-events}",
            groupId = "${kafka.consumer.group-id:notification-service-group}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handlePaymentEvents(NotificationEventDTO event) {
        log.info("Received payment event: {} for tenant: {}", event.getEventType(), event.getTenantId());
        try {
            eventProcessor.processEvent(event);
        } catch (Exception e) {
            log.error("Error processing payment event: {}", event.getEventId(), e);
        }
    }

    /**
     * Listens to refund-related events.
     *
     * @param event The notification event from Kafka
     */
    @KafkaListener(
            topics = "${kafka.topics.refund-events:refund-events}",
            groupId = "${kafka.consumer.group-id:notification-service-group}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleRefundEvents(NotificationEventDTO event) {
        log.info("Received refund event: {} for tenant: {}", event.getEventType(), event.getTenantId());
        try {
            eventProcessor.processEvent(event);
        } catch (Exception e) {
            log.error("Error processing refund event: {}", event.getEventId(), e);
        }
    }
}
