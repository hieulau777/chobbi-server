package com.chobbi.server.order.controller;

import com.chobbi.server.auth.AccountPrincipal;
import com.chobbi.server.exception.BusinessException;
import com.chobbi.server.order.dto.OrderRequest;
import com.chobbi.server.order.dto.PlaceOrderResponse;
import com.chobbi.server.order.dto.ShopOrderDto;
import com.chobbi.server.order.dto.MyOrderDto;
import com.chobbi.server.order.services.OrderServices;
import com.chobbi.server.shop.entity.ShopEntity;
import com.chobbi.server.shop.repo.ShopRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {

    private final OrderServices orderServices;
    private final ShopRepo shopRepo;

    @PostMapping("/place")
    public ResponseEntity<PlaceOrderResponse> placeOrder(
            @AuthenticationPrincipal AccountPrincipal principal,
            @RequestBody List<OrderRequest> req) {
        PlaceOrderResponse response = orderServices.placeOrder(principal.getAccountId(), req);
        return ResponseEntity.ok(response);
    }

    /**
     * Lấy danh sách đơn hàng của shop theo seller đang đăng nhập.
     */
    @GetMapping("/shop/orders")
    public ResponseEntity<List<ShopOrderDto>> getShopOrders(
            @AuthenticationPrincipal AccountPrincipal principal,
            @RequestParam(name = "status", required = false) String status) {
        ShopEntity shop = shopRepo.findByAccountEntity_IdAndDeletedAtIsNull(principal.getAccountId())
                .orElseThrow(() -> new BusinessException("Bạn chưa có shop", HttpStatus.BAD_REQUEST));
        String effectiveStatus = (status == null || status.isBlank()) ? "PENDING" : status;
        List<ShopOrderDto> orders = orderServices.getOrdersByShopAndStatus(shop.getId(), effectiveStatus);
        return ResponseEntity.ok(orders);
    }

    @PostMapping("/ship")
    public ResponseEntity<?> shipOrders(
            @AuthenticationPrincipal AccountPrincipal principal,
            @RequestBody List<Long> orderIds) {
        orderServices.markOrdersAsShipped(principal.getAccountId(), orderIds);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/cancel")
    public ResponseEntity<?> cancelOrders(
            @AuthenticationPrincipal AccountPrincipal principal,
            @RequestBody List<Long> orderIds) {
        orderServices.cancelOrders(principal.getAccountId(), orderIds);
        return ResponseEntity.ok().build();
    }

    /**
     * Lấy danh sách đơn hàng của tài khoản mua hàng đang đăng nhập.
     *
     * 1 order group có thể có 1 hoặc nhiều shop, mỗi shop có 1 hoặc nhiều sản phẩm đã đặt,
     * kèm thông tin product + variation (tên combination) để hiển thị.
     */
    @GetMapping("/my")
    public ResponseEntity<List<MyOrderDto>> getMyOrders(
            @AuthenticationPrincipal AccountPrincipal principal,
            @RequestParam(name = "status", required = false) String status
    ) {
        List<MyOrderDto> orders = orderServices.getOrdersOfAccount(principal.getAccountId(), status);
        return ResponseEntity.ok(orders);
    }
}
