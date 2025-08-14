// NotificationUserRepository.java
package com.phungquocthai.symphony.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.phungquocthai.symphony.entity.NotificationUser;
import com.phungquocthai.symphony.entity.Notification;

@Repository
public interface NotificationUserRepository extends JpaRepository<NotificationUser, Integer> {

    @Query("SELECT nu.notification FROM NotificationUser nu " +
           "WHERE nu.user.userId = :userId " +
           "ORDER BY nu.notification.createdAt DESC")
    List<Notification> findNotificationsByUserId(Integer userId);

    @Modifying
    @Transactional
    @Query("UPDATE NotificationUser nu SET nu.isRead = true " +
           "WHERE nu.notification.notificationId = :notificationId " +
           "AND nu.user.userId = :userId")
    int markAsRead(Integer notificationId, Integer userId);
}
