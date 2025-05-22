package com.thathsara.notification_service.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NotificationCreateResponse {
    private Long id;
    private String message;
}
