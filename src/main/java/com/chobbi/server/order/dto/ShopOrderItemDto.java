package com.chobbi.server.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShopOrderItemDto {
    private String productName;
    private String productThumbnail;
    private String variationName;
    private Integer quantity;
    private BigDecimal price;
}
