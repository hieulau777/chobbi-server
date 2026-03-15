package com.chobbi.server.order.services;

import com.chobbi.server.order.dto.OrderRequest;
import com.chobbi.server.order.dto.PlaceOrderResponse;
import com.chobbi.server.order.dto.ShopOrderDto;
import com.chobbi.server.order.dto.MyOrderDto;

import java.util.List;

public interface OrderServices {
    PlaceOrderResponse placeOrder(Long accountId, List<OrderRequest> req);

    List<ShopOrderDto> getOrdersByShop(Long shopId);

    List<ShopOrderDto> getOrdersByShopAndStatus(Long shopId, String status);

    void markOrdersAsShipped(Long sellerAccountId, List<Long> orderIds);

    void cancelOrders(Long sellerAccountId, List<Long> orderIds);

    /**
     * Lấy danh sách đơn hàng (order group) của tài khoản mua hàng đang đăng nhập.
     *
     * Mỗi group có thể chứa 1 hoặc nhiều shop, mỗi shop có nhiều sản phẩm/biến thể.
     */
    List<MyOrderDto> getOrdersOfAccount(Long accountId, String status);
}
