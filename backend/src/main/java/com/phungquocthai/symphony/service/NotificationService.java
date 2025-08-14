package com.phungquocthai.symphony.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.phungquocthai.symphony.dto.NotificationDTO;
import com.phungquocthai.symphony.entity.Notification;
import com.phungquocthai.symphony.entity.NotificationUser;
import com.phungquocthai.symphony.entity.User;
import com.phungquocthai.symphony.repository.NotificationRepository;
import com.phungquocthai.symphony.repository.NotificationUserRepository;
import com.phungquocthai.symphony.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationUserRepository notificationUserRepository;
    private final UserRepository userRepository;

    public NotificationDTO sendNotificationToUser(Integer senderId, Integer receiverId, String message, String type) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found with id: " + senderId));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Receiver not found with id: " + receiverId));

        Notification notification = Notification.builder()
                .message(message)
                .type(type)
                .sender(sender)
                .build();

        notificationRepository.save(notification);

        NotificationUser notificationUser = NotificationUser.builder()
                .notification(notification)
                .user(receiver)
                .isRead(false)
                .build();

        notificationUserRepository.save(notificationUser);

        return mapToDTO(notification, receiver.getUserId(), false);
    }

    public NotificationDTO sendNotificationToAllUsers(Integer senderId, String message, String type) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found with id: " + senderId));

        List<User> allUsers = userRepository.findAll();

        Notification notification = Notification.builder()
                .message(message)
                .type(type)
                .sender(sender)
                .build();

        notificationRepository.save(notification);

        List<NotificationUser> notificationUsers = allUsers.stream()
                .map(user -> NotificationUser.builder()
                        .notification(notification)
                        .user(user)
                        .isRead(false)
                        .build())
                .toList();

        notificationUserRepository.saveAll(notificationUsers);

        return mapToDTO(notification, sender.getUserId(), false);
    }

    public List<NotificationDTO> getNotificationsForUser(Integer userId) {
        return notificationUserRepository.findNotificationsByUserId(userId).stream()
                .map(n -> {
                    Boolean isRead = n.getNotificationUsers().stream()
                            .filter(nu -> nu.getUser().getUserId().equals(userId))
                            .findFirst()
                            .map(NotificationUser::getIsRead)
                            .orElse(false);
                    return mapToDTO(n, userId, isRead);
                })
                .toList();
    }

    public void markNotificationAsRead(Integer notificationId, Integer userId) {
        int updatedRows = notificationUserRepository.markAsRead(notificationId, userId);
        if (updatedRows == 0) {
            throw new RuntimeException("Không tìm thấy thông báo hoặc đã được đánh dấu đọc.");
        }
    }

    private NotificationDTO mapToDTO(Notification notification, Integer userId, Boolean isRead) {
        return NotificationDTO.builder()
                .notificationId(notification.getNotificationId())
                .message(notification.getMessage())
                .type(notification.getType())
                .createdAt(notification.getCreatedAt())
                .senderId(notification.getSender() != null ? notification.getSender().getUserId() : null)
                .isRead(isRead)
                .build();
    }
}
