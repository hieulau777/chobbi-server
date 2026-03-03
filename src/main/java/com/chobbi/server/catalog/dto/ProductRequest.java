package com.chobbi.server.catalog.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequest {
    private Long shopId;
    private Long productId; // Bắt buộc để tìm sản phẩm [10, 11]
    @NotNull
    private Long categoryId; // Để check case đổi category [10, 12]
    @NotBlank
    private String name;
    @NotBlank
    private String description;

    @NotNull(message = "Trọng lượng sản phẩm là bắt buộc")
    @Min(value = 0, message = "Trọng lượng phải >= 0 (gram)")
    private Long weight;

    private List<ProductImageDto> images; // Case 1 & 2 của Images
    private List<ProductAttributeDto> attributes; // Tái sử dụng Create DTO [13, 14]
    private List<ProductTierDto> tiers; // Case 3 của Tiers
    private List<ProductOptionImageDto> optionImages; // Dành cho Tier hasImages = true
    private List<ProductVariationDto> variations; // Case 1 & 2 của Variations
}
