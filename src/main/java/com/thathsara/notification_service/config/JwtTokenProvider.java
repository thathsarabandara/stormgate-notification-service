package com.thathsara.notification_service.config;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

/**
 * Utility class for JWT token operations.
 * Handles token generation, validation, and claim extraction.
 */
@Slf4j
@Component
public class JwtTokenProvider {

    /**
     * JWT secret key.
     */
    @Value("${jwt.secret}")
    private String jwtSecret;

    /**
     * JWT expiration time in milliseconds.
     */
    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    /**
     * Generate JWT token.
     *
     * @param userId The user ID
     * @param tenantId The tenant ID
     * @param username The username
     * @return JWT token
     */
    public String generateToken(String userId, String tenantId, String username) {
        final Map<String, Object> claims = new HashMap<>();
        claims.put("tenant_id", tenantId);
        claims.put("user_id", userId);

        return createToken(claims, username);
    }

    /**
     * Create JWT token.
     *
     * @param claims The token claims
     * @param subject The subject (username)
     * @return JWT token
     */
    private String createToken(Map<String, Object> claims, String subject) {
        final Date now = new Date();
        final Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()), io.jsonwebtoken.SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Get user ID from token.
     *
     * @param token The JWT token
     * @return User ID
     */
    public String getUserIdFromToken(String token) {
        final Claims claims = getAllClaimsFromToken(token);
        return claims.get("user_id", String.class);
    }

    /**
     * Get tenant ID from token.
     *
     * @param token The JWT token
     * @return Tenant ID
     */
    public String getTenantIdFromToken(String token) {
        final Claims claims = getAllClaimsFromToken(token);
        return claims.get("tenant_id", String.class);
    }

    /**
     * Get username from token.
     *
     * @param token The JWT token
     * @return Username
     */
    public String getUsernameFromToken(String token) {
        final Claims claims = getAllClaimsFromToken(token);
        return claims.getSubject();
    }

    /**
     * GET expiration date from token.
     *
     * @param token The JWT token
     * @return Expiration date
     */
    public Date getExpirationDateFromToken(String token) {
        final Claims claims = getAllClaimsFromToken(token);
        return claims.getExpiration();
    }

    /**
     * Get all claims from token.
     *
     * @param token The JWT token
     * @return Claims object
     */
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Check if token is expired.
     *
     * @param token The JWT token
     * @return true if token is expired
     */
    private Boolean isTokenExpired(String token) {
        try {
            final Date expiration = getExpirationDateFromToken(token);
            return expiration.before(new Date());
        } catch (Exception e) {
            log.error("Error checking token expiration", e);
            return true;
        }
    }

    /**
     * Validate JWT token.
     *
     * @param token The JWT token
     * @return true if token is valid
     */
    public Boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                    .build()
                    .parseSignedClaims(token);

            return !isTokenExpired(token);
        } catch (Exception e) {
            log.error("JWT validation failed: {}", e.getMessage());
            return false;
        }
    }
}
