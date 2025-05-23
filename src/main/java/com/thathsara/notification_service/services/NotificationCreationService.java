package com.thathsara.notification_service.services;

import java.util.List;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.thathsara.notification_service.dtos.NotificationCreateRequest;
import com.thathsara.notification_service.dtos.NotificationCreateResponse;
import com.thathsara.notification_service.entities.Group;
import com.thathsara.notification_service.entities.GroupNotification;
import com.thathsara.notification_service.entities.Notification;
import com.thathsara.notification_service.entities.UserNotification;
import com.thathsara.notification_service.repositories.GroupNotificationRepository;
import com.thathsara.notification_service.repositories.GroupRepository;
import com.thathsara.notification_service.repositories.NotificationRepository;
import com.thathsara.notification_service.repositories.UserNotificationRepository;

import jakarta.transaction.Transactional;

@Service
public class NotificationCreationService {
    /**
     * Repository for managing notifications.
     */
    @Autowired
    private NotificationRepository notificationRepository;

    /**
     * Repository for managing user notifications.
     */
    @Autowired
    private UserNotificationRepository userNotificationRepository;

    /**
     * Repository for managing groups.
     */
    @Autowired
    private GroupRepository groupRepository;

    /**
     * Repository for managing group notifications.
     */
    @Autowired
    private GroupNotificationRepository groupNotificationRepository;


    @Transactional
    public Notification createNotification( Long tenantId, NotificationCreateRequest request) {
        try {
            if (tenantId == null) {
                throw new BadRequestException("Tenant ID is required");
            }

            final Notification.NotificationType type;
            switch (request.getType()) {
                case "SMS" -> type = Notification.NotificationType.SMS;
                case "EMAIL" -> type = Notification.NotificationType.EMAIL;
                case "IN_APP" -> type = Notification.NotificationType.IN_APP;
                case "PUSH" -> type = Notification.NotificationType.PUSH;
                default -> throw new BadRequestException("Invalid notification type: " + request.getType());
            }

            final Notification notification = new Notification();
            notification.setTenantid(tenantId);
            notification.setTitle(request.getTitle());
            notification.setMessage(request.getMessage());
            notification.setType(type);
            notification.setDeleted(false);
            
            final Notification savedNotification = notificationRepository.save(notification);
            return savedNotification;
        } catch (BadRequestException e) {
            return null;
        } catch (Exception e) {
            return null;
        }
    }
    @Transactional
    public ResponseEntity<NotificationCreateResponse> notifyUsers(Notification notification, List<Long> userIds) {
        try {
            for (Long userId : userIds) {
                final UserNotification userNotification = new UserNotification();
                userNotification.setNotification(notification);
                userNotification.setUserId(userId);
                userNotification.setIsRead(false);
                userNotificationRepository.save(userNotification);
            }
            return ResponseEntity
            .ok(new NotificationCreateResponse(notification.getId(), "Successfully Created the User Notification"));
        } catch (Exception e) {
            return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new NotificationCreateResponse(notification.getId(), "User Notification Creation Failed"));
        }
    }
    @Transactional
    public ResponseEntity<NotificationCreateResponse> notifyGroup(Notification notification, Group group) {
        try {
            final GroupNotification groupNotification = new GroupNotification();
            groupNotification.setNotification(notification);
            groupNotification.setGroup(group);
            groupNotificationRepository.save(groupNotification);
            return ResponseEntity
            .ok(new NotificationCreateResponse(notification.getId(), "Successfully Created the Group Notification"));
        } catch (Exception e) {
            return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new NotificationCreateResponse(notification.getId(), "Group Notification Creation Failed"));
        }
    }

    public Group findOrCreateGroup(String groupName, Long tenantId) {
        try {
            final Group group = groupRepository.findByTenantIdandName(tenantId, groupName.toUpperCase());
            if (group == null) {
                final Group newGroup = new Group();
                newGroup.setName(groupName);
                newGroup.setTenantid(tenantId);
                groupRepository.save(newGroup);
                return newGroup;
            }
            return group;
        } catch (Exception e) {
            return null;
        }
    }
}

