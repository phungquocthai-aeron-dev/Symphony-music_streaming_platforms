package com.phungquocthai.symphony.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDTO {
    private Integer notificationId;
    private Boolean isRead;
    private String message;
    private String type;
    private LocalDateTime createdAt;
    private Integer senderId;
    private List<Integer> recipientIds;
}
