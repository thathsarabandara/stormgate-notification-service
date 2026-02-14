package com.thathsara.notification_service.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.thathsara.notification_service.dtos.NotificationDeleteResponse;
import com.thathsara.notification_service.entities.Notification;
import com.thathsara.notification_service.repositories.NotificationRepository;

/**
 * Unit tests for NotificationDeleteService.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationDeleteService Tests")
class NotificationDeleteServiceTests {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationDeleteService deleteService;

    private Long notificationId;
    private Long tenantId;
    private Notification notification;

    @BeforeEach
    void setUp() {
        notificationId = 1L;
        tenantId = 100L;

        notification = Notification.builder()
                .id(notificationId)
                .tenantid(tenantId)
                .title("Test Notification")
                .message("This is a test notification")
                .isDeleted(false)
                .build();
    }

    @Test
    @DisplayName("Should delete notification successfully")
    void testDeleteNotificationSuccess() {
        when(notificationRepository.findByIdAndIsDeleted(notificationId, false))
                .thenReturn(Optional.of(notification));
        when(notificationRepository.save(any(Notification.class)))
                .thenReturn(notification);

        ResponseEntity<NotificationDeleteResponse> response = deleteService.deleteResponse(tenantId, notificationId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(notificationId, response.getBody().getId());
        assertTrue(response.getBody().getMessage().contains("Successfully Deleted"));
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    @DisplayName("Should not delete non-existent notification")
    void testDeleteNonExistentNotification() {
        when(notificationRepository.findByIdAndIsDeleted(notificationId, false))
                .thenReturn(Optional.empty());

        ResponseEntity<NotificationDeleteResponse> response = deleteService.deleteResponse(tenantId, notificationId);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("not found"));
        verify(notificationRepository, never()).save(any(Notification.class));
    }

    @Test
    @DisplayName("Should handle null tenant ID")
    void testDeleteWithNullTenantId() {
        ResponseEntity<NotificationDeleteResponse> response = deleteService.deleteResponse(null, notificationId);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("Tenant ID is required"));
    }

    @Test
    @DisplayName("Should handle null notification ID")
    void testDeleteWithNullNotificationId() {
        ResponseEntity<NotificationDeleteResponse> response = deleteService.deleteResponse(tenantId, null);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("Notification ID is required"));
    }

    @Test
    @DisplayName("Should handle repository exception")
    void testDeleteWithRepositoryException() {
        when(notificationRepository.findByIdAndIsDeleted(notificationId, false))
                .thenThrow(new RuntimeException("Database error"));

        ResponseEntity<NotificationDeleteResponse> response = deleteService.deleteResponse(tenantId, notificationId);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("Failed to delete"));
    }

    @Test
    @DisplayName("Should delete multiple notifications sequentially")
    void testDeleteMultipleNotifications() {
        Long notificationId2 = 2L;
        Notification notification2 = Notification.builder()
                .id(notificationId2)
                .tenantid(tenantId)
                .title("Test Notification 2")
                .message("This is another test notification")
                .isDeleted(false)
                .build();

        when(notificationRepository.findByIdAndIsDeleted(notificationId, false))
                .thenReturn(Optional.of(notification));
        when(notificationRepository.findByIdAndIsDeleted(notificationId2, false))
                .thenReturn(Optional.of(notification2));
        when(notificationRepository.save(any(Notification.class)))
                .thenReturn(notification);

        ResponseEntity<NotificationDeleteResponse> response1 = deleteService.deleteResponse(tenantId, notificationId);
        ResponseEntity<NotificationDeleteResponse> response2 = deleteService.deleteResponse(tenantId, notificationId2);

        assertNotNull(response1);
        assertEquals(HttpStatus.OK, response1.getStatusCode());
        assertNotNull(response2);
        assertEquals(HttpStatus.OK, response2.getStatusCode());
        verify(notificationRepository, times(2)).save(any(Notification.class));
    }
}
