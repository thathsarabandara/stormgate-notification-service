package com.thathsara.notification_service.config;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Unit tests for JwtTokenProvider.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("JwtTokenProvider Tests")
class JwtTokenProviderTests {

    private JwtTokenProvider tokenProvider;
    private static final String SECRET = "cfddd580c0caa2e2cad8b560cec782369e73960d0089949d314ba4ac0062ba803ce9201b0da1a80781b7815dc0f99f3e74";
    private static final long EXPIRATION = 3600000; // 1 hour in milliseconds

    @BeforeEach
    void setUp() {
        tokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(tokenProvider, "jwtSecret", SECRET);
        ReflectionTestUtils.setField(tokenProvider, "jwtExpirationMs", EXPIRATION);
    }

    @Test
    @DisplayName("Should generate valid JWT token")
    void testGenerateToken() {
        String userId = "user-123";
        String tenantId = "tenant-456";
        String username = "testuser";
        String token = tokenProvider.generateToken(userId, tenantId, username);

        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(tokenProvider.validateToken(token));
    }

    @Test
    @DisplayName("Should extract username from token")
    void testGetUsernameFromToken() {
        String userId = "user-123";
        String tenantId = "tenant-456";
        String username = "testuser";
        String token = tokenProvider.generateToken(userId, tenantId, username);

        String extractedUsername = tokenProvider.getUsernameFromToken(token);

        assertEquals(username, extractedUsername);
    }

    @Test
    @DisplayName("Should validate valid token")
    void testValidateValidToken() {
        String userId = "user-123";
        String tenantId = "tenant-456";
        String username = "testuser";
        String token = tokenProvider.generateToken(userId, tenantId, username);

        boolean isValid = tokenProvider.validateToken(token);

        assertTrue(isValid);
    }

    @Test
    @DisplayName("Should invalidate malformed token")
    void testValidateMalformedToken() {
        String malformedToken = "invalid.token.here";

        boolean isValid = tokenProvider.validateToken(malformedToken);

        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should invalidate expired token")
    void testValidateExpiredToken() {
        ReflectionTestUtils.setField(tokenProvider, "jwtExpirationMs", -1000L); // Negative expiration
        String userId = "user-123";
        String tenantId = "tenant-456";
        String username = "testuser";
        String token = tokenProvider.generateToken(userId, tenantId, username);

        boolean isValid = tokenProvider.validateToken(token);

        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should handle empty token")
    void testValidateEmptyToken() {
        boolean isValid = tokenProvider.validateToken("");

        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should handle null token")
    void testValidateNullToken() {
        boolean isValid = tokenProvider.validateToken(null);

        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should extract correct expiration time")
    void testGetExpirationDateFromToken() {
        String userId = "user-123";
        String tenantId = "tenant-456";
        String username = "testuser";
        String token = tokenProvider.generateToken(userId, tenantId, username);

        Date expirationDate = tokenProvider.getExpirationDateFromToken(token);

        assertNotNull(expirationDate);
        assertTrue(expirationDate.after(new Date()));
    }

    @Test
    @DisplayName("Should generate different tokens for different usernames")
    void testGenerateDifferentTokens() {
        String userId1 = "user-123";
        String userId2 = "user-456";
        String tenantId = "tenant-789";
        String token1 = tokenProvider.generateToken(userId1, tenantId, "user1");
        String token2 = tokenProvider.generateToken(userId2, tenantId, "user2");

        assertNotEquals(token1, token2);
        assertEquals("user1", tokenProvider.getUsernameFromToken(token1));
        assertEquals("user2", tokenProvider.getUsernameFromToken(token2));
    }

    @Test
    @DisplayName("Should verify token structure")
    void testTokenStructure() {
        String userId = "user-123";
        String tenantId = "tenant-456";
        String token = tokenProvider.generateToken(userId, tenantId, "testuser");

        // JWT should have 3 parts separated by dots
        String[] parts = token.split("\\.");
        assertEquals(3, parts.length);
    }
}
