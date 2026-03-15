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
public class MyOrderDto {
    private Long orderGroupId;
    private String orderGroupCode;
    private BigDecimal totalAmount;
    private BigDecimal subTotal;
    private LocalDateTime createdAt;
    private List<MyOrderShopDto> shops = new ArrayList<>();
}

