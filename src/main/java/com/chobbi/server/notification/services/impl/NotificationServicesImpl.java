package com.chobbi.server.notification.services.impl;

import com.chobbi.server.notification.NotificationMessageTemplates;
import com.chobbi.server.notification.dto.NotificationDto;
import com.chobbi.server.notification.entity.NotificationEntity;
import com.chobbi.server.notification.enums.NotificationType;
import com.chobbi.server.notification.enums.TargetRole;
import com.chobbi.server.notification.repo.NotificationRepo;
import com.chobbi.server.notification.services.NotificationServices;
import com.chobbi.server.order.entity.OrderEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServicesImpl implements NotificationServices {

    private final NotificationRepo notificationRepo;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public List<NotificationDto> getForSeller(Long accountId) {
        return notificationRepo
                .findByAccountIdAndTargetRoleWithOrder(accountId, TargetRole.SELLER)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<NotificationDto> getForClient(Long accountId) {
        return notificationRepo
                .findByAccountIdAndTargetRoleWithOrder(accountId, TargetRole.BUYER)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private NotificationDto toDto(NotificationEntity n) {
        return NotificationDto.builder()
                .id(n.getId())
                .orderId(n.getOrderEntity() != null ? n.getOrderEntity().getId() : null)
                .type(n.getType())
                .message(n.getMessage())
                .isRead(n.getIsRead())
                .createdAt(n.getCreatedAt())
                .targetRole(n.getTargetRole())
                .build();
    }

    @Override
    public void notifySellerNewOrder(OrderEntity order) {
        if (order == null || order.getShopEntity() == null || order.getShopEntity().getAccountEntity() == null) {
            return;
        }
        NotificationEntity n = new NotificationEntity();
        n.setAccountEntity(order.getShopEntity().getAccountEntity());
        n.setTargetRole(TargetRole.SELLER);
        n.setOrderEntity(order);
        n.setType(NotificationType.ORDER_NEW);
        n.setMessage(NotificationMessageTemplates.render(NotificationMessageTemplates.SELLER_ORDER_NEW, order.getId()));
        n.setIsRead(false);
        notificationRepo.save(n);

        Long sellerAccountId = order.getShopEntity().getAccountEntity().getId();
        NotificationDto dto = toDto(n);
        messagingTemplate.convertAndSendToUser(
                sellerAccountId.toString(),
                "queue/notifications",
                dto
        );
    }

    @Override
    public void notifyBuyerOrderShipped(OrderEntity order) {
        if (order == null || order.getAccountEntity() == null) {
            return;
        }
        NotificationEntity n = new NotificationEntity();
        n.setAccountEntity(order.getAccountEntity());
        n.setTargetRole(TargetRole.BUYER);
        n.setOrderEntity(order);
        n.setType(NotificationType.ORDER_SHIPPING);
        n.setMessage(NotificationMessageTemplates.render(NotificationMessageTemplates.BUYER_ORDER_SHIPPING, order.getId()));
        n.setIsRead(false);
        notificationRepo.save(n);

        Long buyerAccountId = order.getAccountEntity().getId();
        NotificationDto dto = toDto(n);
        messagingTemplate.convertAndSendToUser(
                buyerAccountId.toString(),
                "queue/notifications",
                dto
        );
    }

    @Override
    public void notifyBuyerOrderCancelledBySeller(OrderEntity order) {
        if (order == null || order.getAccountEntity() == null) {
            return;
        }
        NotificationEntity n = new NotificationEntity();
        n.setAccountEntity(order.getAccountEntity());
        n.setTargetRole(TargetRole.BUYER);
        n.setOrderEntity(order);
        n.setType(NotificationType.ORDER_CANCELLED);
        n.setMessage(NotificationMessageTemplates.render(NotificationMessageTemplates.BUYER_ORDER_CANCELLED_BY_SELLER, order.getId()));
        n.setIsRead(false);
        notificationRepo.save(n);

        Long buyerAccountId = order.getAccountEntity().getId();
        NotificationDto dto = toDto(n);
        messagingTemplate.convertAndSendToUser(
                buyerAccountId.toString(),
                "queue/notifications",
                dto
        );
    }
}
