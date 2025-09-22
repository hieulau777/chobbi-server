package com.chobbi.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {
    private Long product_id;
    private String name;
    private List<ProductOptionDto> options;
    private List<ProductVariantDto> variations;
    private List<ProductCategoryDto> categories;
}
