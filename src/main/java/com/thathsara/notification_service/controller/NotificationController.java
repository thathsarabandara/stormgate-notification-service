/**
 * Contains controllers for handling notification service APIs.
 */
package com.thathsara.notification_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.thathsara.notification_service.dtos.AdminNotificationListGetResponse;
import com.thathsara.notification_service.dtos.NotificationCreateRequest;
import com.thathsara.notification_service.dtos.NotificationCreateResponse;
import com.thathsara.notification_service.dtos.NotificationGetListResponse;
import com.thathsara.notification_service.dtos.NotificationReadUnreadResponse;
import com.thathsara.notification_service.entities.Group;
import com.thathsara.notification_service.entities.Notification;
import com.thathsara.notification_service.services.NotificationCreationService;
import com.thathsara.notification_service.services.NotificationGetService;
import com.thathsara.notification_service.services.NotificationReadUnreadService;

/**
 * Controller class for handling notification operations.
 */

@RestController
@RequestMapping("/api/v1/notification")
public class NotificationController {

    /** Service for creating notifications. */
    @Autowired
    private NotificationCreationService notificationCreationService;

    /** Service for geting notifications. */
    @Autowired
    private NotificationGetService notificationGetService;

    /** Service for read unread notifications. */
    @Autowired
    private NotificationReadUnreadService notificationReadUnreadService;

    /**
     * send a new notificaiton
     * 
     * @param tenantid the thenant ID
     * @param request the notification request
     * @return the response Entity
     */
    @PostMapping("/")
    public ResponseEntity<NotificationCreateResponse> sendNotification(
        @RequestHeader(name = "Tenant-Id")  final Long tenantid,
        @ModelAttribute final NotificationCreateRequest request
    ) {
        try {
            if (tenantid == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new NotificationCreateResponse(null, "Missing Tenant Id"));
            }
            final Notification notification = notificationCreationService.createNotification(tenantid, request);


            if (request.getUserIds() != null && !request.getUserIds().isEmpty()) {
                return notificationCreationService.notifyUsers(notification, request.getUserIds());
            }

            if (request.getGroupName() != null && !request.getGroupName().isEmpty()) {
                final Group group = notificationCreationService.findOrCreateGroup(request.getGroupName(), tenantid);
                return notificationCreationService.notifyGroup(notification, group);
            }

            return ResponseEntity.status(HttpStatus.CREATED)
            .body(new NotificationCreateResponse(notification.getId(), "Notification sent successfully."));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new NotificationCreateResponse(null, "Notification Creation Failed"));
        }
    }
    /**
     * Retrieves notifications for a user.
     *
     * @param tenantid the tenant ID
     * @param userid   the user ID
     * @param page     the page number
     * @param limit    the page size
     * @return the response entity
     */

    @GetMapping("/user/{userid}/notifications")
    public  ResponseEntity<NotificationGetListResponse> getUserNotifi(
        @RequestHeader(name = "Tenant-Id") final Long tenantid,
        @PathVariable final Long userid,
        @RequestParam(defaultValue = "1") final int page,
        @RequestParam(defaultValue = "20") final int limit
    ) {
        return notificationGetService.getUserNotifiations(tenantid, userid, page, limit);
    }

    /**
     * Retrieves notifications for a group.
     *
     * @param tenantid  the tenant ID
     * @param groupName the group name
     * @param page      the page number
     * @param limit     the page size
     * @return the response entity
     */

    @GetMapping("/user/{groupname}/notifications")
    public  ResponseEntity<NotificationGetListResponse> getGroupNotifi(
        @RequestHeader(name = "Tenant-Id") final Long tenantid,
        @PathVariable final String groupName,
        @RequestParam(defaultValue = "1") final int page,
        @RequestParam(defaultValue = "20") final int limit
    ) {
        return notificationGetService.getGroupNotifications(tenantid, groupName, page, limit);
    }

    /**
     * Marks a user notification as read.
     *
     * @param tenantid       the tenant ID
     * @param notificationid the notification ID
     * @param userid         the user ID
     * @return the response entity
     */

    @PutMapping("/user/{userid}/notification/{notificationid}/read")
    public ResponseEntity<NotificationReadUnreadResponse> makeReadNotifi(
        @RequestHeader(name = "Tenant-Id") final Long tenantid,
        @PathVariable final Long notificationid,
        @PathVariable final Long userid
    ) {
        return notificationReadUnreadService
        .getReadUnreadUserNotification(tenantid, userid, notificationid, true);
    }

    /**
     * Marks a user notification as unread.
     *
     * @param tenantid       the tenant ID
     * @param notificationid the notification ID
     * @param userid         the user ID
     * @return the response entity
     */

    @PutMapping("/user/{userid}/notification/{notificationid}/unread")
    public ResponseEntity<NotificationReadUnreadResponse> makeUnReadNotifi(
        @RequestHeader(name = "Tenant-Id") final Long tenantid,
        @PathVariable final Long notificationid,
        @PathVariable final Long userid
    ) {
        return notificationReadUnreadService
        .getReadUnreadUserNotification(tenantid, userid, notificationid, false);
    }

    /**
     * Marks a group notification as read for a user.
     *
     * @param tenantid       the tenant ID
     * @param notificationid the notification ID
     * @param userid         the user ID
     * @param groupName      the group name
     * @return the response entity
     */

    @PutMapping("group/{groupName}/user/{userid}/notification/{notificationid}/read")
    public ResponseEntity<NotificationReadUnreadResponse> makeReadGroupNotifi(
        @RequestHeader(name = "Tenant-Id") final Long tenantid,
        @PathVariable final Long notificationid,
        @PathVariable final Long userid,
        @PathVariable final String groupName
    ) {
        return notificationReadUnreadService
        .getReadUnreadUserGroupNotification(tenantid, userid, groupName, notificationid, true);
    }

    /**
     * Marks a group notification as unread for a user.
     *
     * @param tenantid       the tenant ID
     * @param notificationid the notification ID
     * @param userid         the user ID
     * @param groupName      the group name
     * @return the response entity
     */

    @PutMapping("group/{groupName}/user/{userid}/notification/{notificationid}/unread")
    public ResponseEntity<NotificationReadUnreadResponse> makeUnReadGroupNotifi(
        @RequestHeader(name = "Tenant-Id") final Long tenantid,
        @PathVariable final Long notificationid,
        @PathVariable final Long userid,
        @PathVariable final String groupName
    ) {
        return notificationReadUnreadService
        .getReadUnreadUserGroupNotification(tenantid, userid, groupName, notificationid, true);
    }

    /**
     * Retrieves all notifications for an admin.
     *
     * @param tenantid the tenant ID
     * @param page     the page number
     * @param limit    the page size
     * @return the response entity
     */

    @GetMapping("/")
    public ResponseEntity<AdminNotificationListGetResponse> getAll(
        @RequestHeader(name = "Tenant-Id") final Long tenantid,
        @RequestParam(defaultValue = "1") final int page,
        @RequestParam(defaultValue = "20") final int limit
    ) {
        return notificationGetService.getAll(tenantid, page, limit);
    }
}
