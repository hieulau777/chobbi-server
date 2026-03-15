package com.chobbi.server.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ShopCategoryResponse {
    private Long id;
    private String name;
    private Integer sortOrder;
    private Boolean isActive;
    private Long shopId;
    /** Danh sách sản phẩm thuộc danh mục (có thể rỗng). */
    private List<ShopCategoryProductDto> products;
}

