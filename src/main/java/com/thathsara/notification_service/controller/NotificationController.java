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

import com.thathsara.notification_service.dtos.NotificationCreateRequest;
import com.thathsara.notification_service.dtos.NotificationCreateResponse;
import com.thathsara.notification_service.dtos.NotificationGetListResponse;
import com.thathsara.notification_service.dtos.NotificationReadUnreadResponse;
import com.thathsara.notification_service.entities.Group;
import com.thathsara.notification_service.entities.Notification;
import com.thathsara.notification_service.services.NotificationCreationService;
import com.thathsara.notification_service.services.NotificationGetService;
import com.thathsara.notification_service.services.NotificationReadUnreadService;

@RestController
@RequestMapping("/api/v1/notification")
public class NotificationController {

    @Autowired
    private NotificationCreationService notificationCreationService;

    @Autowired
    private NotificationGetService notificationGetService;

    @Autowired
    private NotificationReadUnreadService notificationReadUnreadService;

    @PostMapping("/")
    public ResponseEntity<NotificationCreateResponse> sendNotification(
        @RequestHeader(name="Tenant-Id") Long tenantid,
        @ModelAttribute NotificationCreateRequest request
    ) {
        try {
            if(tenantid == null){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new NotificationCreateResponse(null, "Missing Tenant Id"));
            }
            Notification notification = notificationCreationService.createNotification(tenantid,request);


            if (request.getUserIds() != null && !request.getUserIds().isEmpty()) {
                return notificationCreationService.notifyUsers(notification, request.getUserIds());
            }

            if (request.getGroupName() != null && !request.getGroupName().isEmpty()) {
                Group group = notificationCreationService.findOrCreateGroup(request.getGroupName(), tenantid);
                return notificationCreationService.notifyGroup(notification, group);
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(new NotificationCreateResponse(notification.getId(), "Notification sent successfully."));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new NotificationCreateResponse(null, "Notification Creation Failed"));
        }
    }
    @GetMapping("/user/{userid}")
    public  ResponseEntity<NotificationGetListResponse> getUserNotifi(
        @RequestHeader(name="Tenant-Id") Long tenantid,
        @PathVariable Long userid,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "20") int limit
    ) {
        return notificationGetService.getUserNotifiations(tenantid, userid, page, limit);
    }
    @GetMapping("/user/{groupname}")
    public  ResponseEntity<NotificationGetListResponse> getGroupNotifi(
        @RequestHeader(name="Tenant-Id") Long tenantid,
        @PathVariable String groupName,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "20") int limit
    ) {
        return notificationGetService.getGroupNotifications(tenantid, groupName, page, limit);
    }

    @PutMapping("/user/{userid}/notification/{notificationid}/read")
    public ResponseEntity<NotificationReadUnreadResponse> makeReadNotifi(
        @RequestHeader(name="Tenant-Id") Long tenantid,
        @PathVariable Long notificationid,
        @PathVariable Long userid
    ) {
        return notificationReadUnreadService.getReadUnreadUserNotification(tenantid, userid, notificationid, true);
    }

    @PutMapping("/user/{userid}/notification/{notificationid}/unread")
    public ResponseEntity<NotificationReadUnreadResponse> makeUnReadNotifi(
        @RequestHeader(name="Tenant-Id") Long tenantid,
        @PathVariable Long notificationid,
        @PathVariable Long userid
    ) {
        return notificationReadUnreadService.getReadUnreadUserNotification(tenantid, userid, notificationid, false);
    }

    @PutMapping("group/{groupName}/user/{userid}/notification/{notificationid}/read")
    public ResponseEntity<NotificationReadUnreadResponse> makeReadGroupNotifi(
        @RequestHeader(name="Tenant-Id") Long tenantid,
        @PathVariable Long notificationid,
        @PathVariable Long userid,
        @PathVariable String groupName
    ) {
        return notificationReadUnreadService.getReadUnreadUserGroupNotification(tenantid, userid, groupName, notificationid, true);
    }

    @PutMapping("group/{groupName}/user/{userid}/notification/{notificationid}/unread")
    public ResponseEntity<NotificationReadUnreadResponse> makeUnReadGroupNotifi(
        @RequestHeader(name="Tenant-Id") Long tenantid,
        @PathVariable Long notificationid,
        @PathVariable Long userid,
        @PathVariable String groupName
    ) {
        return notificationReadUnreadService.getReadUnreadUserGroupNotification(tenantid, userid, groupName, notificationid, true);
    }
}
