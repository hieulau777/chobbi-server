package com.chobbi.server.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MiniCartItemDto {
    private Long productId;
    private String productName;
    private String imageUrl;
    private BigDecimal price;
}

