package com.thathsara.notification_service.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.thathsara.notification_service.dtos.NotificationDeleteResponse;
import com.thathsara.notification_service.entities.Notification;
import com.thathsara.notification_service.repositories.NotificationRepository;

import jakarta.transaction.Transactional;

/**
 * Service class to handle notification deletion operations.
 */
@Service
public class NotificationDeleteService {

    /**
     * Repository for managing notifications.
     */
    @Autowired
    private NotificationRepository notificationRepository;

    /**
     * Marks a notification as deleted for a given tenant and notification ID.
     *
     * @param tenantId       The tenant ID to which the notification belongs.
     * @param notificationId The ID of the notification to be deleted.
     * @return ResponseEntity containing the delete response status and message.
     */
    @Transactional
    public ResponseEntity<NotificationDeleteResponse> deleteResponse(Long tenantId, Long notificationId)  {
        try {
            if (tenantId == null) {
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new NotificationDeleteResponse( null, "Tenant ID is required"));
            }
            if (notificationId == null) {
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new NotificationDeleteResponse( null, "Notification ID is required"));
            }

            final Optional<Notification> notification = notificationRepository.findByIdAndIsDeleted(tenantId, false);

            if (!notification.isPresent()) {
                return ResponseEntity
                    .status(HttpStatus.NO_CONTENT)
                    .body(new NotificationDeleteResponse( null, "Notification is not found"));
            }

            final Notification notifi = notification.get();
            notifi.setDeleted(true);
            notificationRepository.save(notifi);

            return ResponseEntity
            .ok(new NotificationDeleteResponse(notification.get().getId(), "Successfully Deleted the Notification"));
        } catch (Exception e) {
            return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new NotificationDeleteResponse(null, "Failed to delete the notification"));
        }
    }
}
