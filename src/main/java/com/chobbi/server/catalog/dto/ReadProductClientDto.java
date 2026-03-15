package com.chobbi.server.catalog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReadProductClientDto {
    private Long productId;
    private String productName;
    private String description;
    private Long weight;
    private Long shopId;
    private String shopName;
    private String shopAvatar;
    private List<ReadProductImageDto> images;
    private List<ReadProductTierDto> tiers;
    private List<ReadProductOptionImagesDto> optionImages;
    /**
     * Chỉ chứa các thuộc tính đã được set cho sản phẩm,
     * mỗi item chỉ gồm tên field và giá trị hiển thị.
     */
    private List<ClientProductAttributeDto> attributes;
    private List<CategoryDto> categoryTree;
    private Long selectedCategoryId;
    private List<ReadProductVariationDto> variations;
}

