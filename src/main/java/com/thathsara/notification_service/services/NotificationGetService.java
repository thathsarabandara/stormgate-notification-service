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

import com.thathsara.notification_service.dtos.AdminNotificationGetResponse;
import com.thathsara.notification_service.dtos.AdminNotificationListGetResponse;
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
    /**
     * Repository to access notification data.
     */
    @Autowired
    private NotificationRepository notificationRepository;

    /**
     * Repository to access user notification data.
     */
    @Autowired
    private UserNotificationRepository userNotificationRepository;

    /**
     * Repository to access group data.
     */
    @Autowired
    private GroupRepository groupRepository;

    /**
     * Repository to access group notification data.
     */
    @Autowired
    private GroupNotificationRepository groupNotificationRepository;

    /**
     * Retrieves a paginated list of notifications for a specific user within a tenant,
     * filtering out notifications older than three months and excluding deleted notifications.
     *
     * <p>This method performs the following:
     * <ul>
     *   <li>Validates that both tenant ID and user ID are provided.</li>
     *   <li>Fetches user notifications from the database created within the last three months.</li>
     *   <li>Filters out notifications that are marked as deleted.</li>
     *   <li>Maps the valid notifications into response objects.</li>
     *   <li>Returns a paginated list of these notifications wrapped in a {@link ResponseEntity}.</li>
     * </ul>
     *
     * @param tenantid the ID of the tenant; must not be {@code null}
     * @param userId the ID of the user for whom notifications are being retrieved; must not be {@code null}
     * @param page the page number for pagination (0-based)
     * @param limit the number of notifications per page
     * @return a {@link ResponseEntity} containing a {@link NotificationGetListResponse}:
     *         <ul>
     *             <li>{@code 200 OK} with the list of notifications if successful</li>
     *             <li>{@code 400 BAD_REQUEST} if either tenant ID or user ID is missing</li>
     *             <li>{@code 500 INTERNAL_SERVER_ERROR} if an unexpected error occurs</li>
     *         </ul>
     */
    @Transactional
    public ResponseEntity<NotificationGetListResponse> 
        getUserNotifiations(Long tenantid, Long userId, int page, int limit) {
        try {
            if (tenantid == null) {
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new NotificationGetListResponse(
                        null, 
                        null,
                        "Tenant ID is required"
                    ));
            }
            if (userId == null) {
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new NotificationGetListResponse(
                        null, 
                        null,
                        "User ID is required"
                    ));
            }

            final LocalDateTime threeMonthsAgo = LocalDateTime.now().minusMonths(3);
            final Pageable pageable = PageRequest.of(page, limit);

            final Page<UserNotification> userNotificationsPage = 
                userNotificationRepository.findAllByUserId(userId, threeMonthsAgo, pageable);

            final List<NotificationGetResponse> notificationResponses = new ArrayList<>();

            for (UserNotification userNotification : userNotificationsPage.getContent()) {
                final Optional<Notification> notificationOpt 
                    = notificationRepository
                        .findByIdAndNotDelated(userNotification.getNotification().getId(), false);

                if (notificationOpt.isPresent()) {
                    final Notification notification = notificationOpt.get();
                    final NotificationGetResponse resp = new NotificationGetResponse(
                        notification.getId(),
                        notification.getTitle(),
                        notification.getMessage(),
                        userNotification.getIsRead(),
                        userNotification.getCreatedAt()
                    );
                    notificationResponses.add(resp);
                }
            }

            final NotificationGetListResponse response 
                = new NotificationGetListResponse(
                    userId, 
                    notificationResponses, 
                    "Notifications fetched successfully"
                );
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new NotificationGetListResponse(
                    null, 
                    null,
                    "An unexpected error occurred: " + e.getMessage()
                ));
        }
    }

    /**
     * Retrieves a paginated list of notifications for a specific group within a tenant,
     * filtering out notifications older than three months.
     *
     * <p>This method performs the following:
     * <ul>
     *   <li>Validates that both tenant ID and group name are provided.</li>
     *   <li>Finds the group entity associated with the given tenant ID and group name.</li>
     *   <li>Fetches group notifications created within the last three months from the database.</li>
     *   <li>Maps the retrieved notifications into response DTOs.</li>
     *   <li>Returns a paginated list of these notifications wrapped in a {@link ResponseEntity}.</li>
     * </ul>
     *
     * @param tenantid the ID of the tenant; must not be {@code null}
     * @param groupName the name of the group whose notifications are to be fetched; must not be {@code null} or empty
     * @param page the page number for pagination (0-based)
     * @param limit the number of notifications per page
     * @return a {@link ResponseEntity} containing a {@link NotificationGetListResponse}:
     *         <ul>
     *             <li>{@code 200 OK} with the list of notifications if successful</li>
     *             <li>{@code 400 BAD_REQUEST} if tenant ID or group name is missing or invalid</li>
     *             <li>{@code 500 INTERNAL_SERVER_ERROR} if an unexpected error occurs</li>
     *         </ul>
     */
    @Transactional
    public ResponseEntity<NotificationGetListResponse> 
        getGroupNotifications(Long tenantid, String groupName, int page, int limit) {
        try {
            if (tenantid == null) {
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new NotificationGetListResponse(
                        null, 
                        null, 
                        "Tenant ID is required"
                    ));
            }
            if (groupName == null || groupName.isEmpty()) {
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new NotificationGetListResponse(
                        null, 
                        null, 
                        "Group name is required"
                    ));
            }
            final Group group = groupRepository.findByTenantIdandName(tenantid, groupName.toUpperCase());

            if (group == null) {
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new NotificationGetListResponse(
                        null, 
                        null, 
                        "No group Found under your tenant"
                    ));
            }

            final LocalDateTime threeMonthsAgo = LocalDateTime.now().minusMonths(3);
            final Pageable pageable = PageRequest.of(page, limit);

            final Page<GroupNotification> groupNotificationsPage = 
                groupNotificationRepository.findAllByGroup(group, threeMonthsAgo, pageable);

            final List<NotificationGetResponse> notificationResponses = new ArrayList<>();

            for (GroupNotification groupNotification : groupNotificationsPage.getContent()) {
                final Optional<Notification> notificationOpt 
                    = notificationRepository.findById(groupNotification.getNotification().getId());
                if (notificationOpt.isPresent()) {
                    final Notification notification = notificationOpt.get();
                    final NotificationGetResponse resp = new NotificationGetResponse(
                        notification.getId(),
                        notification.getTitle(),
                        notification.getMessage(),
                        false,
                        groupNotification.getCreatedAt()
                    );
                    notificationResponses.add(resp);
                }
            }

            final NotificationGetListResponse response = new NotificationGetListResponse(
                group.getId(), 
                notificationResponses, 
                "Group notifications fetched successfully"
            );
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new NotificationGetListResponse(
                    null, 
                    null, 
                    "An unexpected error occurred: " + e.getMessage())
                );
        }
    }

    /**
     * Retrieves a paginated list of all notifications for a specific tenant, including details
     * about user and group associations where applicable.
     *
     * <p>This method performs the following:
     * <ul>
     *   <li>Validates that the tenant ID is provided.</li>
     *   <li>Fetches notifications from the database for the given tenant using pagination.</li>
     *   <li>For each notification:
     *     <ul>
     *       <li>Checks if it is associated with a user notification and retrieves the user ID and read status.</li>
     *       <li>Checks if it is associated with a group notification and retrieves the group name.</li>
     *       <li>Maps notification details into {@link AdminNotificationGetResponse} objects.</li>
     *     </ul>
     *   </li>
     *   <li>Returns a paginated list of these notifications wrapped 
     *      in an {@link AdminNotificationListGetResponse}.</li>
     * </ul>
     *
     * @param tenantId the ID of the tenant; must not be {@code null}
     * @param page the page number for pagination (0-based)
     * @param limit the number of notifications per page
     * @return a {@link ResponseEntity} containing an {@link AdminNotificationListGetResponse}:
     *         <ul>
     *             <li>{@code 200 OK} with the list of notifications if successful</li>
     *             <li>{@code 400 BAD_REQUEST} if the tenant ID is missing</li>
     *             <li>{@code 500 INTERNAL_SERVER_ERROR} if an unexpected error occurs</li>
     *         </ul>
     */
    @Transactional
    public ResponseEntity<AdminNotificationListGetResponse> getAll(Long tenantId, int page, int limit) {
        try {
            if (tenantId == null) {
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new AdminNotificationListGetResponse(null, "Tenant ID is required"));
            }

            final Pageable pageable = PageRequest.of(page, limit);
            final Page<Notification> notifications = notificationRepository.findByTenantId(tenantId, pageable);

            final List<AdminNotificationGetResponse> notifiList = new java.util.ArrayList<>();

            for (Notification notification : notifications.getContent()) {
                final Optional<UserNotification> userNotificationOpt 
                    = userNotificationRepository.findByNotificationAndUserId(notification, tenantId);
                final Optional<GroupNotification> groupNotificationOpt 
                    = groupNotificationRepository.findbyNotification(notification);

                final AdminNotificationGetResponse response = new AdminNotificationGetResponse();
                response.setNotifiId(notification.getId());
                response.setTitle(notification.getTitle());
                response.setMessage(notification.getMessage());
                response.setType(notification.getType().name());
                response.setDeleted(notification.isDeleted());
                response.setCratedAt(notification.getCreatedAt());

                if (userNotificationOpt.isPresent()) {
                    final UserNotification userNotification = userNotificationOpt.get();
                    response.setUserId(userNotification.getUserId());
                    response.setUserRead(userNotification.getIsRead());
                }

                if (groupNotificationOpt.isPresent()) {
                    final GroupNotification groupNotification = groupNotificationOpt.get();
                    final Optional<Group> group = groupRepository.findById(groupNotification.getGroup().getId());
                    response.setGroupName(group.get().getName());
                }

                notifiList.add(response);
            }

            return ResponseEntity
                .ok(new AdminNotificationListGetResponse(notifiList, "All notifications fetched successfully"));

        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new AdminNotificationListGetResponse(
                    null, 
                    "Failed to get All Notification Data: " + e.getMessage()
                ));
        }
    }
}
