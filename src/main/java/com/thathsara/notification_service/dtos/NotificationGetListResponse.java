package com.thathsara.notification_service.dtos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO representing the response containing a list of notifications for a user.
 */
@Data
@AllArgsConstructor
public class NotificationGetListResponse {

    /**
     * The ID of the user for whom notifications are retrieved.
     */
    private Long userid;

    /**
     * The list of notifications retrieved for the user.
     */
    private List<NotificationGetResponse> notificationGetResponses;

    /**
     * The status or message regarding the notification retrieval result.
     */
    private String message;
}
