package com.chobbi.server.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MyOrderShopDto {
    private Long orderId;
    private Long shopId;
    private String shopName;
    private String shippingName;
    private BigDecimal totalPrice;
    private BigDecimal shippingCost;
    private String status;
    private List<MyOrderItemDto> items = new ArrayList<>();
}

