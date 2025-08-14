package com.phungquocthai.symphony.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.phungquocthai.symphony.dto.NotificationDTO;
import com.phungquocthai.symphony.service.NotificationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationDTO>> getUserNotifications(@PathVariable Integer userId) {
        return ResponseEntity.ok(notificationService.getNotificationsForUser(userId));
    }

    @PostMapping("/send-to-user/{userId}")
    public ResponseEntity<NotificationDTO> sendToUser(
            @PathVariable Integer userId,
            @RequestParam Integer sendId,
            @RequestParam String message,
            @RequestParam String type) {
        return ResponseEntity.ok(notificationService.sendNotificationToUser(sendId, userId, message, type));
    }

    @PostMapping("/send-to-all")
    public ResponseEntity<NotificationDTO> sendToAll(
            @RequestParam Integer sendId,
            @RequestParam String message,
            @RequestParam String type) {
        return ResponseEntity.ok(notificationService.sendNotificationToAllUsers(sendId, message, type));
    }

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<String> markAsRead(
            @PathVariable int notificationId,
            @RequestParam int userId) {
        notificationService.markNotificationAsRead(notificationId, userId);
        return ResponseEntity.ok("Notification marked as read.");
    }
}
