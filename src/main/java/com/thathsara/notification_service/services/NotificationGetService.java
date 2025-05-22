package com.thathsara.notification_service.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.thathsara.notification_service.dtos.NotificationGetListResponse;
import com.thathsara.notification_service.dtos.NotificationGetResponse;
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
public class NotificationGetService {
    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserNotificationRepository userNotificationRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GroupNotificationRepository groupNotificationRepository;

    @Transactional
    public ResponseEntity<NotificationGetListResponse> getUserNotifiations(Long tenantid, Long userId, int page, int limit) {
        try {
            if (tenantid == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new NotificationGetListResponse(null, null,"Tenant ID is required"));
            }
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new NotificationGetListResponse(null, null,"User ID is required"));
            }

            LocalDateTime threeMonthsAgo = LocalDateTime.now().minusMonths(3);
            Pageable pageable = PageRequest.of(page, limit);

            Page<UserNotification> userNotificationsPage = 
                userNotificationRepository.findAllByUserId(userId, threeMonthsAgo, pageable);

            List<NotificationGetResponse> notificationResponses = new ArrayList<>();

            for (UserNotification userNotification : userNotificationsPage.getContent()) {
                Optional<Notification> notificationOpt = notificationRepository.findByIdAndNotDelated(userNotification.getNotification().getId(), false);
                if (notificationOpt.isPresent()) {
                    Notification notification = notificationOpt.get();
                    NotificationGetResponse resp = new NotificationGetResponse(
                        notification.getId(),
                        notification.getTitle(),
                        notification.getMessage(),
                        userNotification.getIsRead(),
                        userNotification.getCreatedAt()
                    );
                    notificationResponses.add(resp);
                }
            }

            NotificationGetListResponse response = new NotificationGetListResponse(userId, notificationResponses, "Notifications fetched successfully");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new NotificationGetListResponse(null, null,"An unexpected error occurred: " + e.getMessage()));
        }
    }
    @Transactional
    public ResponseEntity<NotificationGetListResponse> getGroupNotifications(Long tenantid, String groupName, int page, int limit) {
        try {
            if (tenantid == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new NotificationGetListResponse(null, null, "Tenant ID is required"));
            }
            if (groupName == null || groupName.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new NotificationGetListResponse(null, null, "Group name is required"));
            }
            Group group = groupRepository.findByTenantIdandName(tenantid, groupName.toUpperCase());
            if(group==null){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new NotificationGetListResponse(null, null, "No group Found under your tenant"));
            }

            LocalDateTime threeMonthsAgo = LocalDateTime.now().minusMonths(3);
            Pageable pageable = PageRequest.of(page, limit);

            Page<GroupNotification> groupNotificationsPage = 
                groupNotificationRepository.findAllByGroup(group, threeMonthsAgo, pageable);

            List<NotificationGetResponse> notificationResponses = new ArrayList<>();

            for (GroupNotification groupNotification : groupNotificationsPage.getContent()) {
                Optional<Notification> notificationOpt = notificationRepository.findById(groupNotification.getNotification().getId());
                if (notificationOpt.isPresent()) {
                    Notification notification = notificationOpt.get();
                    NotificationGetResponse resp = new NotificationGetResponse(
                        notification.getId(),
                        notification.getTitle(),
                        notification.getMessage(),
                        false,
                        groupNotification.getCreatedAt()
                    );
                    notificationResponses.add(resp);
                }
            }

            NotificationGetListResponse response = new NotificationGetListResponse(
                group.getId(), notificationResponses, "Group notifications fetched successfully"
            );
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new NotificationGetListResponse(null, null, "An unexpected error occurred: " + e.getMessage()));
        }
    }

}
