package com.chobbi.server.notification.services;

import com.chobbi.server.notification.dto.NotificationDto;
import com.chobbi.server.order.entity.OrderEntity;

import java.util.List;

public interface NotificationServices {

    /**
     * Lấy danh sách thông báo cho Seller (target_role = SELLER).
     */
    List<NotificationDto> getForSeller(Long accountId);

    /**
     * Lấy danh sách thông báo cho Buyer/Client (target_role = BUYER).
     */
    List<NotificationDto> getForClient(Long accountId);

    /**
     * Tạo thông báo đơn mới cho Seller khi có order được đặt.
     */
    void notifySellerNewOrder(OrderEntity order);

    /**
     * Thông báo cho Buyer khi đơn được Seller chuyển sang trạng thái SHIPPED.
     */
    void notifyBuyerOrderShipped(OrderEntity order);

    /**
     * Thông báo cho Buyer khi đơn bị Seller hủy.
     */
    void notifyBuyerOrderCancelledBySeller(OrderEntity order);
}
