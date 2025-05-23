package com.thathsara.notification_service.dtos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO for creating a new notification request.
 */
@Data
@AllArgsConstructor
public class NotificationCreateRequest {

    /**
     * The title of the notification.
     */
    private String title;

    /**
     * The message content of the notification.
     */
    private String message;

    /**
     * The type/category of the notification.
     */
    private String type;

    /**
     * List of user IDs to whom the notification should be sent.
     */
    private List<Long> userIds;

    /**
     * The email address associated with the notification (if applicable).
     */
    private String email;

    /**
     * The name of the group to which this notification should be sent.
     */
    private String groupName;
}
