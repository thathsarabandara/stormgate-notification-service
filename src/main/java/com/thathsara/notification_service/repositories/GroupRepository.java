package com.thathsara.notification_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thathsara.notification_service.entities.Group;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    
}
