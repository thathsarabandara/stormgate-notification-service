package com.thathsara.notification_service.dtos;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationGetResponse {
    private String title;
    private String message;
    private boolean isRead;
    private LocalDateTime createdDate;
}
