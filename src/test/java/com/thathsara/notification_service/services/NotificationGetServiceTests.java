package com.thathsara.notification_service.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.thathsara.notification_service.dtos.AdminNotificationGetResponse;
import com.thathsara.notification_service.dtos.AdminNotificationListGetResponse;
import com.thathsara.notification_service.dtos.NotificationGetListResponse;
import com.thathsara.notification_service.entities.Group;
import com.thathsara.notification_service.entities.GroupNotification;
import com.thathsara.notification_service.entities.Notification;
import com.thathsara.notification_service.entities.Notification.NotificationType;
import com.thathsara.notification_service.entities.UserNotification;
import com.thathsara.notification_service.repositories.GroupNotificationRepository;
import com.thathsara.notification_service.repositories.GroupRepository;
import com.thathsara.notification_service.repositories.NotificationRepository;
import com.thathsara.notification_service.repositories.UserNotificationRepository;

/**
 * Unit tests for NotificationGetService.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationGetService Tests")
class NotificationGetServiceTests {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserNotificationRepository userNotificationRepository;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private GroupNotificationRepository groupNotificationRepository;

    @InjectMocks
    private NotificationGetService getService;

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
    @DisplayName("Should get user notifications successfully with pagination")
    void testGetUserNotificationsSuccess() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<UserNotification> page = new PageImpl<>(List.of(userNotification), pageable, 1);

        when(userNotificationRepository.findAllByUserId(eq(userId), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(page);
        when(notificationRepository.findByIdAndIsDeleted(eq(notificationId), eq(false)))
                .thenReturn(Optional.of(notification));

        ResponseEntity<NotificationGetListResponse> response = 
                getService.getUserNotifiations(tenantId, userId, 0, 20);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(userId, response.getBody().getUserid());
        assertEquals(1, response.getBody().getNotificationGetResponses().size());
        assertEquals("Notifications fetched successfully", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should return bad request when tenant ID is null")
    void testGetUserNotificationsNullTenantId() {
        ResponseEntity<NotificationGetListResponse> response = 
                getService.getUserNotifiations(null, userId, 0, 20);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Tenant ID is required", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should return bad request when user ID is null")
    void testGetUserNotificationsNullUserId() {
        ResponseEntity<NotificationGetListResponse> response = 
                getService.getUserNotifiations(tenantId, null, 0, 20);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("User ID is required", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should return empty notification list for user with no notifications")
    void testGetUserNotificationsEmpty() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<UserNotification> emptyPage = new PageImpl<>(new ArrayList<>(), pageable, 0);

        when(userNotificationRepository.findAllByUserId(eq(userId), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(emptyPage);

        ResponseEntity<NotificationGetListResponse> response = 
                getService.getUserNotifiations(tenantId, userId, 0, 20);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getNotificationGetResponses().isEmpty());
    }

    @Test
    @DisplayName("Should get group notifications successfully")
    void testGetGroupNotificationsSuccess() {
        Group group = new Group();
        group.setId(groupId);
        group.setTenantId(tenantId);
        group.setName("TEST_GROUP");

        GroupNotification groupNotification = new GroupNotification();
        groupNotification.setId(1L);
        groupNotification.setNotification(notification);
        groupNotification.setGroup(group);
        groupNotification.setCreatedAt(now);

        Pageable pageable = PageRequest.of(0, 20);
        Page<GroupNotification> page = new PageImpl<>(List.of(groupNotification), pageable, 1);

        when(groupRepository.findByTenantIdAndName(eq(tenantId), eq("TEST_GROUP"))).thenReturn(group);
        when(groupNotificationRepository.findAllByGroup(any(Group.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(page);
        when(notificationRepository.findById(eq(notificationId))).thenReturn(Optional.of(notification));

        ResponseEntity<NotificationGetListResponse> response = 
                getService.getGroupNotifications(tenantId, "TEST_GROUP", 0, 20);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getNotificationGetResponses().size());
        assertEquals("Group notifications fetched successfully", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should return bad request when group not found")
    void testGetGroupNotificationsGroupNotFound() {
        when(groupRepository.findByTenantIdAndName(eq(tenantId), eq("NONEXISTENT"))).thenReturn(null);

        ResponseEntity<NotificationGetListResponse> response = 
                getService.getGroupNotifications(tenantId, "NONEXISTENT", 0, 20);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("No group Found under your tenant", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should get all notifications for admin successfully")
    void testGetAllNotificationsSuccess() {
        AdminNotificationGetResponse adminResponse = new AdminNotificationGetResponse();
        adminResponse.setNotifiId(notificationId);
        adminResponse.setTitle(notification.getTitle());
        adminResponse.setMessage(notification.getMessage());
        adminResponse.setType(notification.getType().name());
        adminResponse.setDeleted(false);
        adminResponse.setCratedAt(now);

        Pageable pageable = PageRequest.of(0, 20);
        Page<Notification> page = new PageImpl<>(List.of(notification), pageable, 1);

        when(notificationRepository.findByTenantid(eq(tenantId), any(Pageable.class))).thenReturn(page);
        when(userNotificationRepository.findByNotificationAndUserId(any(Notification.class), eq(tenantId)))
                .thenReturn(Optional.of(userNotification));
        when(groupNotificationRepository.findByNotification(any(Notification.class)))
                .thenReturn(Optional.empty());

        ResponseEntity<AdminNotificationListGetResponse> response = 
                getService.getAll(tenantId, 0, 20);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("All notifications fetched successfully", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should return bad request when tenant ID is null for getAll")
    void testGetAllNotificationsNullTenantId() {
        ResponseEntity<AdminNotificationListGetResponse> response = 
                getService.getAll(null, 0, 20);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Tenant ID is required", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should handle exceptions in getUserNotifications")
    void testGetUserNotificationsException() {

        when(userNotificationRepository.findAllByUserId(eq(userId), any(LocalDateTime.class), any(Pageable.class)))
                .thenThrow(new RuntimeException("Database error"));

        ResponseEntity<NotificationGetListResponse> response = 
                getService.getUserNotifiations(tenantId, userId, 0, 20);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("An unexpected error occurred"));
    }

}
