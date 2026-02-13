package com.thathsara.notification_service.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thathsara.notification_service.entities.UserPreference;

/**
 * Repository interface for UserPreference entity.
 */
@Repository
public interface UserPreferenceRepository extends JpaRepository<UserPreference, UUID> {

    /**
     * Find user preferences by user ID and tenant ID.
     *
     * @param userId The user ID
     * @param tenantId The tenant ID
     * @return Optional user preference
     */
    Optional<UserPreference> findByUserIdAndTenantId(UUID userId, UUID tenantId);

    /**
     * Check if user prefers a specific notification channel.
     *
     * @param userId The user ID
     * @param tenantId The tenant ID
     * @return Optional user preference
     */
    Optional<UserPreference> findByUserIdAndTenantIdAndEmailEnabledTrue(UUID userId, UUID tenantId);
}
