package com.chobbi.server.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShopOrderDto {
    private Long orderId;
    private String orderGroupCode;
    private String buyerEmail;
    private String shippingName;
    private BigDecimal totalPrice;
    private BigDecimal shippingCost;
    private LocalDateTime createdAt;
    private String status;
    private List<ShopOrderItemDto> items = new ArrayList<>();
}
