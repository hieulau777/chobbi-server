package com.chobbi.server.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
public class PublicShopProductDto {
    private Long id;
    private String name;
    private String thumbnail;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    /** Tổng tồn kho các variation. */
    private Integer totalStock;
    /** Id danh mục con của shop (trong list shop_categories), null nếu sản phẩm chưa gán danh mục. */
    private Long shopCategoryId;
}
