package com.chobbi.server.notification.enums;

/**
 * Loại nghiệp vụ thông báo.
 * ORDER_NEW: Khách chốt đơn → gửi Seller.
 * ORDER_SHIPPING: Seller nhấn giao hàng → gửi Buyer.
 * ORDER_CANCELLED: Một bên hủy đơn → gửi bên còn lại.
 */
public enum NotificationType {
    ORDER_NEW,
    ORDER_SHIPPING,
    ORDER_CANCELLED
}
