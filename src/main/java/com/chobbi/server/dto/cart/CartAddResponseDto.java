package com.chobbi.server.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartAddResponseDto {
    private Long shopId;
    private Long productId;
    private BigDecimal priceUnit;
}
