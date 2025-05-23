package com.thathsara.notification_service.dtos;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminNotificationGetResponse {
    /**
     * Notification ID
     */
    private Long notifiId;

    /**
     * Title of the notification
     */
    private String title;

    /**
     * Notification message
     */
    private String message;

    /**
     * Type of the notification
     */
    private String type;

    /**
     * Flag indicating if notification is deleted
     */
    private boolean isDeleted;

    /**
     * Creation timestamp
     */
    private LocalDateTime cratedAt;

    /**
     * User ID associated with the notification
     */
    private Long userId;

    /**
     * Flag indicating if user has read the notification
     */
    private boolean isUserRead;

    /**
     * Group name associated with the notification
     */
    private String groupName;
}
