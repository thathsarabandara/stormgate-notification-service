package com.thathsara.notification_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.thathsara.notification_service.entities.UserGroup;

public interface UserGroupRepository extends JpaRepository<UserGroup, Long> {
    
}
