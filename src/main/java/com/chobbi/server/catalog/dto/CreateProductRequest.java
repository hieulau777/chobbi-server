package com.chobbi.server.catalog.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
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
    @Size(min = 1, max = 9)
    private List<CreateProductImages> images;
    @NotEmpty
    @Valid
    private List<ProductAttributeDto> attributes;
    @Valid
    @Size(min = 1, max = 2)
    private List<ProductTierDto> tiers;
    private List<CreateProductOptionImages> optionImages;
    @Valid
    @Size(min = 1, max = 50)
    private List<ProductVariationDto> variations;
}
