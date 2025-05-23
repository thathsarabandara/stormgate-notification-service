package com.thathsara.notification_service.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO representing the response after creating a notification.
 */
@Data
@AllArgsConstructor
public class NotificationCreateResponse {

    /**
     * The unique ID of the created notification.
     */
    private Long id;

    /**
     * The status or message regarding the notification creation result.
     */
    private String message;
}
