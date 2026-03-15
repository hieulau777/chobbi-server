package com.chobbi.server.shop.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateShopCategoryRequest {

    @NotBlank
    private String name;

    private Integer sortOrder;

    private Boolean isActive;

    /**
     * Danh sách productId thuộc shop muốn gán vào category này.
     * Có thể rỗng hoặc null.
     */
    private List<Long> productIds;
}

