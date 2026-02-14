package com.thathsara.notification_service.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.thathsara.notification_service.dtos.NotificationListResponse;
import com.thathsara.notification_service.entities.NotificationLog;
import com.thathsara.notification_service.repositories.NotificationLogRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * REST controller for notification retrieval and management.
 */
@Slf4j
@RestController
@RequestMapping("/notifications")
public class NotificationRetrievalController {

    /**
     * Notification log repository.
     */
    @Autowired
    private NotificationLogRepository notificationLogRepository;

    /**
     * Get notifications for the logged-in user.
     *
     * @param authentication The authentication object
     * @param status The notification status filter (optional)
     * @param page The page number
     * @param size The page size
     * @return Paginated list of notifications
     */
    @GetMapping
    public ResponseEntity<Page<NotificationListResponse>> getNotifications(
            Authentication authentication,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("Fetching notifications for user: {}", authentication.getName());

        // Extract tenant and user from token (implementation depends on your security setup)
        final UUID userId = UUID.fromString(authentication.getName());
        final UUID tenantId = (UUID) authentication.getCredentials(); // Example

        try {
            final List<NotificationLog> logs;
            if (status != null && !status.isEmpty()) {
                // Filter by status
                logs = notificationLogRepository.findByUserIdAndTenantId(userId, tenantId)
                        .stream()
                        .filter(log -> log.getStatus().toString().equalsIgnoreCase(status))
                        .toList();
            } else {
                logs = notificationLogRepository.findByUserIdAndTenantId(userId, tenantId);
            }

            // Convert to response DTOs
            final List<NotificationListResponse> responses = logs.stream()
                    .map(this::mapToResponse)
                    .toList();

            // Manual pagination
            final int start = page * size;
            final int end = Math.min(start + size, responses.size());
            final List<NotificationListResponse> pageContent = responses.subList(start, end);

            final Page<NotificationListResponse> result = new PageImpl<>(
                    pageContent,
                    Pageable.ofSize(size).withPage(page),
                    responses.size()
            );

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error fetching notifications", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Mark a notification as read.
     *
     * @param notificationId The notification ID
     * @param authentication The authentication object
     * @return Response with updated notification
     */
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<NotificationListResponse> markAsRead(
            @PathVariable UUID notificationId,
            Authentication authentication) {

        log.info("Marking notification {} as read for user: {}", notificationId, authentication.getName());

        try {
            final NotificationLog log = notificationLogRepository.findById(notificationId)
                    .orElse(null);

            if (log == null) {
                return ResponseEntity.notFound().build();
            }

            log.setStatus(NotificationLog.DeliveryStatus.READ);
            log.setReadAt(java.time.LocalDateTime.now());

            final NotificationLog updated = notificationLogRepository.save(log);

            return ResponseEntity.ok(mapToResponse(updated));
        } catch (Exception e) {
            log.error("Error marking notification as read", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Map entity to response DTO.
     *
     * @param log The notification log
     * @return Response DTO
     */
    private NotificationListResponse mapToResponse(NotificationLog log) {
        return NotificationListResponse.builder()
                .id(log.getId())
                .tenantId(log.getTenantId())
                .userId(log.getUserId())
                .eventType(log.getEventType().toString())
                .channel(log.getChannel().toString())
                .status(log.getStatus().toString())
                .subject(log.getSubject())
                .content(log.getContent())
                .readAt(log.getReadAt())
                .sentAt(log.getSentAt())
                .createdAt(log.getCreatedAt())
                .build();
    }
}
