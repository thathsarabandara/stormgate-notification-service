package com.thathsara.notification_service.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing the response after deleting a notification.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDeleteResponse {

    /**
     * The unique ID of the deleted notification.
     */
    private Long id;

    /**
     * The status or message regarding the notification deletion result.
     */
    private String message;
}
