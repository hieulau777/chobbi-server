package com.chobbi.server.catalog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO cho card sản phẩm trên listing (trang chủ, trang category).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCardDto {
    private Long productId;
    private Long shopId;
    private String productName;
    private String thumbnail;
    private BigDecimal price;
}
