// NotificationRepository.java
package com.phungquocthai.symphony.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.phungquocthai.symphony.entity.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {
}
