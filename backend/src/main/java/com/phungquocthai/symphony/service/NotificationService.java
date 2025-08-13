package com.phungquocthai.symphony.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.phungquocthai.symphony.dto.NotificationDTO;
import com.phungquocthai.symphony.entity.Notification;
import com.phungquocthai.symphony.entity.User;
import com.phungquocthai.symphony.mapper.NotificationMapper;
import com.phungquocthai.symphony.repository.NotificationRepository;
import com.phungquocthai.symphony.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {

	@Autowired
    NotificationRepository notificationRepository;
	
	@Autowired
    UserRepository userRepository;
	
	@Autowired
	NotificationMapper notificationMapper;

    public NotificationDTO sendNotificationToUser(Integer sendId, Integer userId, String message, String type) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        Notification notification = Notification.builder()
                .message(message)
                .type(type)
                .isRead(false)
                .user(userRepository.findById(sendId).orElseThrow())
                .users(Set.of(user))
                .build();

        Notification saved = notificationRepository.save(notification);
        return notificationMapper.toDTO(saved);
    }

    public NotificationDTO sendNotificationToAllUsers(Integer sendId, String message, String type) {
        List<User> allUsers = userRepository.findAll();

        Notification notification = Notification.builder()
                .message(message)
                .type(type)
                .isRead(false)
                .user(userRepository.findById(sendId).orElseThrow())
                .users(Set.copyOf(allUsers))
                .build();

        Notification saved = notificationRepository.save(notification);
        return notificationMapper.toDTO(saved);
    }
    
    public List<NotificationDTO> getNotificationsForUser(Integer userId) {
        return notificationRepository.findByRecipientId(userId).stream()
                .map(notificationMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    
}
