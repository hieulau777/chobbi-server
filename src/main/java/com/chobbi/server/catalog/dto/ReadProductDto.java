package com.chobbi.server.catalog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReadProductDto {
    private Long productId;
    private String productName;
    private String description;
    private List<ReadProductImageDto> images;
    private List<ReadProductTierDto> tiers;
    private List<ReadProductOptionImagesDto> optionImages;
    private List<ReadProductAttributes> attributes;
    private List<ReadProductSelectedAttributes> selectedAttributes;
    private List<CategoryDto> categoryTree;
    private Long selectedCategoryId;
    private List<ReadProductVariationDto> variations;
}
