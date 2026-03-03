package com.chobbi.server.notification.dto;

import com.chobbi.server.notification.enums.NotificationType;
import com.chobbi.server.notification.enums.TargetRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {
    private Long id;
    private Long orderId;
    private NotificationType type;
    private String message;
    private Boolean isRead;
    private LocalDateTime createdAt;
    private TargetRole targetRole;
}
