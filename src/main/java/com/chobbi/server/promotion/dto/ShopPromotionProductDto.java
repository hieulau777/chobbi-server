package com.chobbi.server.promotion.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class ShopPromotionProductDto {
    private Long id;
    private String name;
    private String thumbnail;
    /** Giá thấp nhất trong các variation (null nếu không có). */
    private BigDecimal minPrice;
    /** Tổng tồn kho các variation. */
    private Integer totalStock;
}

