package com.thathsara.notification_service.dtos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NotificationCreateRequest {
    private String title;
    private String message;
    private String type;
    private List<Long> userIds;
    private String email;
    private String groupName;
}
