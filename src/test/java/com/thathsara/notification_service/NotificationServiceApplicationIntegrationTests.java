package com.thathsara.notification_service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for NotificationServiceApplication.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("NotificationServiceApplication Integration Tests")
class NotificationServiceApplicationIntegrationTests {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    @DisplayName("Should load application context")
    void contextLoads() {
        assertNotNull(applicationContext);
    }

    @Test
    @DisplayName("Should contain NotificationTemplateService bean")
    void shouldContainNotificationTemplateServiceBean() {
        assertTrue(applicationContext.containsBean("notificationTemplateService"));
    }

    @Test
    @DisplayName("Should contain UserPreferenceService bean")
    void shouldContainUserPreferenceServiceBean() {
        assertTrue(applicationContext.containsBean("userPreferenceService"));
    }

    @Test
    @DisplayName("Should contain NotificationReadUnreadService bean")
    void shouldContainNotificationReadUnreadServiceBean() {
        assertTrue(applicationContext.containsBean("notificationReadUnreadService"));
    }

    @Test
    @DisplayName("Should contain NotificationDeleteService bean")
    void shouldContainNotificationDeleteServiceBean() {
        assertTrue(applicationContext.containsBean("notificationDeleteService"));
    }

    @Test
    @DisplayName("Should contain NotificationGetService bean")
    void shouldContainNotificationGetServiceBean() {
        assertTrue(applicationContext.containsBean("notificationGetService"));
    }

    @Test
    @DisplayName("Should contain JwtTokenProvider bean")
    void shouldContainJwtTokenProviderBean() {
        assertTrue(applicationContext.containsBean("jwtTokenProvider"));
    }

    @Test
    @DisplayName("Should contain SecurityConfig bean")
    void shouldContainSecurityConfigBean() {
        assertTrue(applicationContext.containsBean("securityConfig"));
    }
}
