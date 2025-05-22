package com.thathsara.notification_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thathsara.notification_service.dtos.NotificationCreateRequest;
import com.thathsara.notification_service.dtos.NotificationCreateResponse;
import com.thathsara.notification_service.entities.Group;
import com.thathsara.notification_service.entities.Notification;
import com.thathsara.notification_service.services.NotificationCreationService;

@RestController
@RequestMapping("/api/v1/notification")
public class NotificationController {

    @Autowired
    private NotificationCreationService notificationCreationService;

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
}
