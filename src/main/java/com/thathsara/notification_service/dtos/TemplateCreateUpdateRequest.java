package com.thathsara.notification_service.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating or updating notification templates.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemplateCreateUpdateRequest {

    /**
     * Type of event this template is for.
     */
    private String eventType;

    /**
     * Channel through which notification is sent.
     */
    private String channel;

    /**
     * Subject of the notification.
     */
    private String subject;

    /**
     * Body/template content with placeholders.
     */
    private String body;

    /**
     * Whether this template is enabled.
     */
    @Builder.Default
    private Boolean isEnabled = true;
}
