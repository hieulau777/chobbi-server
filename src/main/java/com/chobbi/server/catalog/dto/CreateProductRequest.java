package com.chobbi.server.catalog.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
    @NotEmpty
    @Valid
    private List<CreateProductAttributeDto> attributes;
    @Valid
    @Size(min = 1, max = 2)
    private List<CreateProductTierDto> tiers;
    @Valid
    @Size(min = 1)
    private List<CreateProductVariationDto> variations;
}
