package com.chobbi.server.notification.entity;

import com.chobbi.server.account.entity.AccountEntity;
import com.chobbi.server.common.BaseEntity;
import com.chobbi.server.notification.enums.NotificationType;
import com.chobbi.server.notification.enums.TargetRole;
import com.chobbi.server.order.entity.OrderEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "notifications")
public class NotificationEntity extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private AccountEntity accountEntity;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_role", nullable = false, length = 20)
    private TargetRole targetRole;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity orderEntity;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 30)
    private NotificationType type;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;
}
