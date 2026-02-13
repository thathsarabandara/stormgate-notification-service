package com.thathsara.notification_service.services;

import java.util.Map;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * Service to handle template resolution with payload variables.
 */
@Slf4j
@Service
public class TemplateResolutionService {

    /**
     * Resolve template placeholders with event data.
     *
     * @param template The template text with placeholders
     * @param payload The event payload
     * @return Resolved template text
     */
    public String resolveTemplate(String template, Object payload) {
        String result = template;

        if (payload instanceof Map) {
            @SuppressWarnings("unchecked")
            final Map<String, Object> map = (Map<String, Object>) payload;

            for (Map.Entry<String, Object> entry : map.entrySet()) {
                final String placeholder = entry.getKey();
                final String value = entry.getValue() != null ? entry.getValue().toString() : "";
                result = result.replace(placeholder, value);
            }
        }

        return result;
    }
}
