package com.chobbi.server.catalog.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductRequest {
    private Long shopId;
    private String name;
    private String description;
    private Long categoryId;
    private String imgCover;
    private List<String> imgList;
    private List<CreateProductAttributeDto> attributes;
    @Size(min = 1, max = 2)
    private List<CreateProductTierDto> tiers;
    private List<CreateProductVariationDto> variations;
}
