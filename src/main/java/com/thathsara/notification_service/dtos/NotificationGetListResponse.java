package com.thathsara.notification_service.dtos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NotificationGetListResponse {
    private Long userid;
    List<NotificationGetResponse> notificationGetResponses;
    private String message;
}
