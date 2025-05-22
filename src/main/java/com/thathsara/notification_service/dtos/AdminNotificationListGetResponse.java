package com.thathsara.notification_service.dtos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminNotificationListGetResponse {
    List<AdminNotificationGetResponse> notifiList;
    private String message;
}
