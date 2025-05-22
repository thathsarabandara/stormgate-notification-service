package com.thathsara.notification_service.dtos;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminNotificationGetResponse {
    private Long notifiId;
    private String title;
    private String message;
    private String type;
    private boolean isDeleted;
    private LocalDateTime cratedAt;
    private Long userId;
    private boolean isUserRead;
    private String groupName;
}
