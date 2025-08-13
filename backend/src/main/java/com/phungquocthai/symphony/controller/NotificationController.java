package com.phungquocthai.symphony.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.phungquocthai.symphony.dto.NotificationDTO;
import com.phungquocthai.symphony.service.NotificationService;


@RestController
@RequestMapping("/notifications")
public class NotificationController {

	@Autowired
	NotificationService notificationService;
	
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
    
}
