package com.thathsara.notification_service.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.thathsara.notification_service.dtos.NotificationReadUnreadResponse;
import com.thathsara.notification_service.entities.Group;
import com.thathsara.notification_service.entities.Notification;
import com.thathsara.notification_service.entities.UserGroupNotification;
import com.thathsara.notification_service.entities.UserNotification;
import com.thathsara.notification_service.repositories.GroupRepository;
import com.thathsara.notification_service.repositories.NotificationRepository;
import com.thathsara.notification_service.repositories.UserGroupNotificationRepository;
import com.thathsara.notification_service.repositories.UserNotificationRepository;

import jakarta.transaction.Transactional;

@Service
public class NotificationReadUnreadService {

    /**
     * Repository for accessing user notifications.
     */
    @Autowired
    private UserNotificationRepository userNotificationRepository;

    /**
     * Repository for accessing user group notifications.
     */
    @Autowired
    private UserGroupNotificationRepository userGroupNotificationRepository;

    /**
     * Repository for accessing notifications.
     */
    @Autowired
    private NotificationRepository notificationRepository;

    /**
     * Repository for accessing group.
     */
    @Autowired
    private GroupRepository groupRepoistoy;

    /**
     * Updates the read/unread status of a user notification.
     * <p>
     * This method validates the provided tenant ID, user ID, and notification ID. 
     * If all values are valid and the notification exists, it updates the read status 
     * of the corresponding user notification entry. If any validation fails, 
     * an appropriate HTTP response is returned.
     * </p>
     *
     * @param tenantId       the ID of the tenant to which the user belongs (cannot be null)
     * @param userId         the ID of the user whose notification status is to be updated (cannot be null)
     * @param notificationId the ID of the notification to update (cannot be null)
     * @param isRead         the new read status (true for read, false for unread)
     * @return a {@link ResponseEntity} containing a {@link NotificationReadUnreadResponse} 
     *         with the result of the operation and appropriate HTTP status code:
     *         <ul>
     *             <li>200 OK - if the notification status was successfully updated</li>
     *             <li>400 BAD_REQUEST - if any required ID is missing or invalid, 
     *                 or if the notification or user notification is not found</li>
     *             <li>500 INTERNAL_SERVER_ERROR - if an unexpected error occurs</li>
     *         </ul>
     * @throws Exception if any unexpected error occurs during the operation
     */
    @Transactional
    public ResponseEntity<NotificationReadUnreadResponse> 
        getReadUnreadUserNotification(Long tenantId, Long userId, Long notificationId, boolean isRead) {
        try {
            if (tenantId == null) {
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new NotificationReadUnreadResponse(null, "Tenant Id is not Found"));
            }
            if (userId == null) {
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new NotificationReadUnreadResponse(null, "User Id is not Found"));
            }
            if (notificationId == null) {
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new NotificationReadUnreadResponse(null, "Notification id is not Found"));
            }

            final Optional<Notification> notificationOpt 
                    = notificationRepository.findByIdAndIsDeleted(notificationId, false);

            if (!notificationOpt.isPresent()) {
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new NotificationReadUnreadResponse(null, "Notification is not Found"));
            }

            final Optional<UserNotification> userNotificationOpt 
                = userNotificationRepository.findByNotificationAndUserId(notificationOpt.get(), userId);

            if (!userNotificationOpt.isPresent()) {
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new NotificationReadUnreadResponse(null, "User Notification is not Found"));
            }

            final UserNotification userNotification = userNotificationOpt.get();
            userNotification.setIsRead(isRead);
            userNotificationRepository.save(userNotification);

            return ResponseEntity
                .ok(new NotificationReadUnreadResponse(null, "Updated the Notification Read"));

        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new NotificationReadUnreadResponse(null, "An unexpected error occurred: " + e.getMessage()));
        }
    }

    /**
     * Updates the read/unread status of a user's group notification.
     * <p>
     * This method validates the provided tenant ID, user ID, group name, and notification ID.
     * If all values are valid and both the group and notification exist, it updates the 
     * read status of the corresponding user group notification entry.
     * If any validation fails, or if the group/notification/user group notification is not found, 
     * an appropriate HTTP response is returned.
     * </p>
     *
     * @param tenantId       the ID of the tenant to which the group belongs (cannot be null)
     * @param userId         the ID of the user whose group notification status is to be updated (cannot be null)
     * @param groupName      the name of the group (cannot be null or blank)
     * @param notificationId the ID of the notification to update (cannot be null)
     * @param isRead         the new read status (true for read, false for unread)
     * @return a {@link ResponseEntity} containing a {@link NotificationReadUnreadResponse} 
     *         with the result of the operation and appropriate HTTP status code:
     *         <ul>
     *             <li>200 OK - if the group notification status was successfully updated</li>
     *             <li>400 BAD_REQUEST - if any required value is missing, invalid, or not found in the database</li>
     *             <li>500 INTERNAL_SERVER_ERROR - if an unexpected error occurs</li>
     *         </ul>
     * @throws Exception if any unexpected error occurs during the operation
     */
    @Transactional
    public ResponseEntity<NotificationReadUnreadResponse> 
        getReadUnreadUserGroupNotification
            (Long tenantId, Long userId, String groupName, Long notificationId, boolean isRead) {
        try {
            if (tenantId == null) {
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new NotificationReadUnreadResponse(null, "Tenant Id is not Found"));
            }
            if (userId == null) {
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new NotificationReadUnreadResponse(null, "User Id is not Found"));
            }
            if (notificationId == null) {
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new NotificationReadUnreadResponse(null, "Notification id is not Found"));
            }

            if (groupName == null || groupName.isBlank() || groupName.isEmpty()) {
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new NotificationReadUnreadResponse(null, "Group Name is not Found"));
            }

            final Group group = groupRepoistoy.findByTenantIdAndName(tenantId, groupName);

            if (group == null) {
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new NotificationReadUnreadResponse(null, "Group is not Found"));
            }

            final Optional<Notification> notificationOpt 
                = notificationRepository.findByIdAndIsDeleted(notificationId, false);
            if (!notificationOpt.isPresent()) {
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new NotificationReadUnreadResponse(null, "Notification is not Found"));
            }

            final Optional<UserGroupNotification> userGroupNotificationOpt = userGroupNotificationRepository
                    .findByNotificationAndUserIdAndGroup(notificationOpt.get(), userId, group);

            if (!userGroupNotificationOpt.isPresent()) {
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new NotificationReadUnreadResponse(null, "User Group Notification is not Found"));
            }

            final UserGroupNotification userGroupNotification = userGroupNotificationOpt.get();
            userGroupNotification.setIsRead(isRead);
            userGroupNotificationRepository.save(userGroupNotification);

            return ResponseEntity
                .ok(new NotificationReadUnreadResponse(null, "Updated the Group Notification Read status"));

        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new NotificationReadUnreadResponse(null, "An unexpected error occurred: " + e.getMessage()));
        }
    }
}
