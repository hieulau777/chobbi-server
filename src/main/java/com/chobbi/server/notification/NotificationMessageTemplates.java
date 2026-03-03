package com.chobbi.server.notification;

/**
 * Mẫu nội dung thông báo (render sẵn ở Backend).
 * Placeholder: #{orderId} sẽ được thay bằng orderId thực khi tạo thông báo.
 */
public final class NotificationMessageTemplates {

    private static final String ORDER_PLACEHOLDER = "#{orderId}";

    /** Seller: đơn mới. */
    public static final String SELLER_ORDER_NEW = "🔔 Bạn có đơn hàng mới **" + ORDER_PLACEHOLDER + "** vừa được đặt. Kiểm tra ngay!";
    /** Seller: khách hủy đơn. */
    public static final String SELLER_ORDER_CANCELLED_BY_BUYER = "❌ Đơn hàng **" + ORDER_PLACEHOLDER + "** đã bị khách hàng hủy.";

    /** Buyer: đang giao hàng. */
    public static final String BUYER_ORDER_SHIPPING = "🚚 Đơn hàng **" + ORDER_PLACEHOLDER + "** của bạn đang trên đường giao đến.";
    /** Buyer: seller hủy đơn. */
    public static final String BUYER_ORDER_CANCELLED_BY_SELLER = "🚫 Rất tiếc, đơn hàng **" + ORDER_PLACEHOLDER + "** đã bị người bán hủy.";

    /**
     * Render message từ template bằng cách thay #{orderId} bằng orderId.
     */
    public static String render(String template, Long orderId) {
        if (template == null || orderId == null) return template;
        return template.replace(ORDER_PLACEHOLDER, String.valueOf(orderId));
    }
}
