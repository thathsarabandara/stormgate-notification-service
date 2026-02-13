package com.thathsara.notification_service.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
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

import com.thathsara.notification_service.dtos.NotificationReadUnreadResponse;
import com.thathsara.notification_service.entities.Group;
import com.thathsara.notification_service.entities.Notification;
import com.thathsara.notification_service.entities.Notification.NotificationType;
import com.thathsara.notification_service.entities.UserGroupNotification;
import com.thathsara.notification_service.entities.UserNotification;
import com.thathsara.notification_service.repositories.GroupRepository;
import com.thathsara.notification_service.repositories.NotificationRepository;
import com.thathsara.notification_service.repositories.UserGroupNotificationRepository;
import com.thathsara.notification_service.repositories.UserNotificationRepository;

/**
 * Unit tests for NotificationReadUnreadService.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationReadUnreadService Tests")
class NotificationReadUnreadServiceTests {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserNotificationRepository userNotificationRepository;

    @Mock
    private UserGroupNotificationRepository userGroupNotificationRepository;

    @Mock
    private GroupRepository groupRepository;

    @InjectMocks
    private NotificationReadUnreadService readUnreadService;

    private Long notificationId;
    private Long userId;
    private Long tenantId;
    private Long groupId;
    private Notification notification;
    private UserNotification userNotification;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        notificationId = 1L;
        userId = 100L;
        tenantId = 1L;
        groupId = 10L;
        now = LocalDateTime.now();

        notification = Notification.builder()
                .id(notificationId)
                .tenantid(tenantId)
                .title("Test Notification")
                .message("This is a test notification")
                .type(NotificationType.EMAIL)
                .isDeleted(false)
                .createdAt(now)
                .updatedAt(now)
                .build();

        userNotification = UserNotification.builder()
                .id(1L)
                .notification(notification)
                .userId(userId)
                .isRead(false)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    @Test
    @DisplayName("Should mark user notification as read successfully")
    void testMarkUserNotificationAsReadSuccess() {
        when(notificationRepository.findByIdAndIsDeleted(eq(notificationId), eq(false)))
                .thenReturn(Optional.of(notification));
        when(userNotificationRepository.findByNotificationAndUserId(any(Notification.class), eq(userId)))
                .thenReturn(Optional.of(userNotification));
        when(userNotificationRepository.save(any(UserNotification.class)))
                .thenReturn(userNotification);

        ResponseEntity<NotificationReadUnreadResponse> response = 
                readUnreadService.getReadUnreadUserNotification(tenantId, userId, notificationId, true);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Updated the Notification Read", response.getBody().getMessage());
        verify(userNotificationRepository, times(1)).save(any(UserNotification.class));
    }

    @Test
    @DisplayName("Should mark user notification as unread successfully")
    void testMarkUserNotificationAsUnreadSuccess() {
        userNotification.setIsRead(true);
        when(notificationRepository.findByIdAndIsDeleted(eq(notificationId), eq(false)))
                .thenReturn(Optional.of(notification));
        when(userNotificationRepository.findByNotificationAndUserId(any(Notification.class), eq(userId)))
                .thenReturn(Optional.of(userNotification));
        when(userNotificationRepository.save(any(UserNotification.class)))
                .thenReturn(userNotification);

        ResponseEntity<NotificationReadUnreadResponse> response = 
                readUnreadService.getReadUnreadUserNotification(tenantId, userId, notificationId, false);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Updated the Notification Read", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should return bad request when tenant ID is null")
    void testMarkNotificationAsReadNullTenantId() {
        ResponseEntity<NotificationReadUnreadResponse> response = 
                readUnreadService.getReadUnreadUserNotification(null, userId, notificationId, true);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Tenant Id is not Found", response.getBody().getMessage());
        verify(notificationRepository, never()).findByIdAndIsDeleted(anyLong(), anyBoolean());
    }

    @Test
    @DisplayName("Should return bad request when user ID is null")
    void testMarkNotificationAsReadNullUserId() {
        ResponseEntity<NotificationReadUnreadResponse> response = 
                readUnreadService.getReadUnreadUserNotification(tenantId, null, notificationId, true);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("User Id is not Found", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should return bad request when notification ID is null")
    void testMarkNotificationAsReadNullNotificationId() {
        ResponseEntity<NotificationReadUnreadResponse> response = 
                readUnreadService.getReadUnreadUserNotification(tenantId, userId, null, true);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Notification id is not Found", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should return bad request when notification is not found")
    void testMarkNotificationAsReadNotFound() {
        when(notificationRepository.findByIdAndIsDeleted(eq(notificationId), eq(false)))
                .thenReturn(Optional.empty());

        ResponseEntity<NotificationReadUnreadResponse> response = 
                readUnreadService.getReadUnreadUserNotification(tenantId, userId, notificationId, true);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Notification is not Found", response.getBody().getMessage());
        verify(userNotificationRepository, never()).save(any(UserNotification.class));
    }

    @Test
    @DisplayName("Should return bad request when user notification is not found")
    void testMarkNotificationAsReadUserNotificationNotFound() {
        when(notificationRepository.findByIdAndIsDeleted(eq(notificationId), eq(false)))
                .thenReturn(Optional.of(notification));
        when(userNotificationRepository.findByNotificationAndUserId(any(Notification.class), eq(userId)))
                .thenReturn(Optional.empty());

        ResponseEntity<NotificationReadUnreadResponse> response = 
                readUnreadService.getReadUnreadUserNotification(tenantId, userId, notificationId, true);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("User Notification is not Found", response.getBody().getMessage());
        verify(userNotificationRepository, never()).save(any(UserNotification.class));
    }

    @Test
    @DisplayName("Should mark group notification as read successfully")
    void testMarkGroupNotificationAsReadSuccess() {
        Group group = new Group();
        group.setId(groupId);
        group.setTenantId(tenantId);
        group.setName("TEST_GROUP");

        UserGroupNotification userGroupNotification = new UserGroupNotification();
        userGroupNotification.setId(1L);
        userGroupNotification.setNotification(notification);
        userGroupNotification.setUserId(userId);
        userGroupNotification.setGroup(group);
        userGroupNotification.setIsRead(false);

        when(notificationRepository.findByIdAndIsDeleted(eq(notificationId), eq(false)))
                .thenReturn(Optional.of(notification));
        when(groupRepository.findByTenantIdAndName(eq(tenantId), eq("TEST_GROUP")))
                .thenReturn(group);
        when(userGroupNotificationRepository.findByNotificationAndUserIdAndGroup(
                any(Notification.class), eq(userId), any(Group.class)))
                .thenReturn(Optional.of(userGroupNotification));
        when(userGroupNotificationRepository.save(any(UserGroupNotification.class)))
                .thenReturn(userGroupNotification);

        ResponseEntity<NotificationReadUnreadResponse> response = 
                readUnreadService.getReadUnreadUserGroupNotification(
                        tenantId, userId, "TEST_GROUP", notificationId, true);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Updated the Group Notification Read status", response.getBody().getMessage());
        verify(userGroupNotificationRepository, times(1)).save(any(UserGroupNotification.class));
    }

    @Test
    @DisplayName("Should return bad request when tenant ID is null for group notification")
    void testMarkGroupNotificationNullTenantId() {
        ResponseEntity<NotificationReadUnreadResponse> response = 
                readUnreadService.getReadUnreadUserGroupNotification(
                        null, userId, "TEST_GROUP", notificationId, true);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Tenant Id is not Found", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should return bad request when group name is null for group notification")
    void testMarkGroupNotificationNullGroupName() {
        ResponseEntity<NotificationReadUnreadResponse> response = 
                readUnreadService.getReadUnreadUserGroupNotification(
                        tenantId, userId, null, notificationId, true);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Group Name is not Found", response.getBody().getMessage());
        verify(notificationRepository, never()).findByIdAndIsDeleted(anyLong(), anyBoolean());
    }

    @Test
    @DisplayName("Should return bad request when group is not found")
    void testMarkGroupNotificationGroupNotFound() {
        when(groupRepository.findByTenantIdAndName(eq(tenantId), eq("NONEXISTENT")))
                .thenReturn(null);

        ResponseEntity<NotificationReadUnreadResponse> response = 
                readUnreadService.getReadUnreadUserGroupNotification(
                        tenantId, userId, "NONEXISTENT", notificationId, true);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Group is not Found", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should handle exceptions in getReadUnreadUserNotification")
    void testMarkNotificationAsReadException() {
        when(notificationRepository.findByIdAndIsDeleted(eq(notificationId), eq(false)))
                .thenThrow(new RuntimeException("Database error"));

        ResponseEntity<NotificationReadUnreadResponse> response = 
                readUnreadService.getReadUnreadUserNotification(tenantId, userId, notificationId, true);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("An unexpected error occurred"));
    }
}
