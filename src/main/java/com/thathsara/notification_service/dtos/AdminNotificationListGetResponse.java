package com.thathsara.notification_service.dtos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
/**
 * Response DTO for admin notification list retrieval.
 */
public class AdminNotificationListGetResponse {
    /**
     * Admin notification types.
     */
    List<AdminNotificationGetResponse> notifiList;
    /**
     * The message of the notification.
     */
    private String message;
}
