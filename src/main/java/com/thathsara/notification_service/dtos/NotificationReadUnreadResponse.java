package com.thathsara.notification_service.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing the response for marking a notification as read or unread.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationReadUnreadResponse {

    /**
     * The unique ID of the notification.
     */
    private Long id;

    /**
     * The message indicating the result of the operation.
     */
    private String message;
}
