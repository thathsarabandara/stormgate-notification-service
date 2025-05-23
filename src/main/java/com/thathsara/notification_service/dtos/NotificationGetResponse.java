package com.thathsara.notification_service.dtos;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing a single notification response.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationGetResponse {

    /**
     * The unique ID of the notification.
     */
    private Long id;

    /**
     * The title of the notification.
     */
    private String title;

    /**
     * The message content of the notification.
     */
    private String message;

    /**
     * Flag indicating whether the notification has been read.
     */
    private boolean isRead;

    /**
     * The date and time when the notification was created.
     */
    private LocalDateTime createdDate;
}
