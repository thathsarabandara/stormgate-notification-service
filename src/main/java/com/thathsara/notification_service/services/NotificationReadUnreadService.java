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
    @Autowired
    private UserNotificationRepository userNotificationRepository;

    @Autowired
    private UserGroupNotificationRepository userGroupNotificationRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private GroupRepository groupRepoistoy;

    @Transactional
    public ResponseEntity<NotificationReadUnreadResponse> getReadUnreadUserNotification(Long tenantId, Long userId, Long notificationId, boolean isRead) {
        try {
            if (tenantId == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new NotificationReadUnreadResponse(null, "Tenant Id is not Found"));
            }
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new NotificationReadUnreadResponse(null, "User Id is not Found"));
            }
            if (notificationId == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new NotificationReadUnreadResponse(null, "Notification id is not Found"));
            }

            Optional<Notification> notificationOpt = notificationRepository.findById(notificationId);
            if (!notificationOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new NotificationReadUnreadResponse(null, "Notification is not Found"));
            }

            Optional<UserNotification> userNotificationOpt = userNotificationRepository.findByNotificationAndUserId(notificationOpt.get(), userId);
            if (!userNotificationOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new NotificationReadUnreadResponse(null, "User Notification is not Found"));
            }

            UserNotification userNotification = userNotificationOpt.get();
            userNotification.setIsRead(isRead);
            userNotificationRepository.save(userNotification);

            return ResponseEntity.ok(new NotificationReadUnreadResponse(null, "Updated the Notification Read"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new NotificationReadUnreadResponse(null, "An unexpected error occurred: " + e.getMessage()));
        }
    }

    @Transactional
    public ResponseEntity<NotificationReadUnreadResponse> getReadUnreadUserGroupNotification(Long tenantId, Long userId, String groupName, Long notificationId, boolean isRead) {
        try {
            if (tenantId == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new NotificationReadUnreadResponse(null, "Tenant Id is not Found"));
            }
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new NotificationReadUnreadResponse(null, "User Id is not Found"));
            }
            if (notificationId == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new NotificationReadUnreadResponse(null, "Notification id is not Found"));
            }

            if (groupName == null || groupName.isBlank() || groupName.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new NotificationReadUnreadResponse(null, "Group Name is not Found"));
            }

            Group group = groupRepoistoy.findByTenantIdandName(tenantId, groupName);

            if(group == null){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new NotificationReadUnreadResponse(null, "Group is not Found"));
            }

            Optional<Notification> notificationOpt = notificationRepository.findById(notificationId);
            if (!notificationOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new NotificationReadUnreadResponse(null, "Notification is not Found"));
            }

            Optional<UserGroupNotification> userGroupNotificationOpt = userGroupNotificationRepository
                    .findByNotificationAndUserIdAndGroup(notificationOpt.get(), userId, group);

            if (!userGroupNotificationOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new NotificationReadUnreadResponse(null, "User Group Notification is not Found"));
            }

            UserGroupNotification userGroupNotification = userGroupNotificationOpt.get();
            userGroupNotification.setIsRead(isRead);
            userGroupNotificationRepository.save(userGroupNotification);

            return ResponseEntity.ok(new NotificationReadUnreadResponse(null, "Updated the Group Notification Read status"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new NotificationReadUnreadResponse(null, "An unexpected error occurred: " + e.getMessage()));
        }
    }
}
