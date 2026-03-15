package com.chobbi.server.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class ShopCategoryProductDto {
    private Long id;
    private String name;
    private String thumbnail;
    /** Giá thấp nhất trong các variation (null nếu không có). */
    private BigDecimal minPrice;
    /** Tổng tồn kho các variation. */
    private Integer totalStock;
}

