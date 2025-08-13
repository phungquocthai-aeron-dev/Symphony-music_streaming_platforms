package com.phungquocthai.symphony.repository;

import com.phungquocthai.symphony.entity.Notification;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {
	@Query("SELECT n FROM Notification n JOIN n.users u WHERE u.userId = :userId ORDER BY n.createdAt DESC")
    List<Notification> findByRecipientId(Integer userId);
}
