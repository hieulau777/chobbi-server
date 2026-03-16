package com.chobbi.server.catalog.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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
    @NotBlank(message = "Tên sản phẩm là bắt buộc")
    @Size(max = 120, message = "Tên sản phẩm tối đa 120 ký tự")
    private String name;
    @Size(max = 3000, message = "Mô tả sản phẩm tối đa 3000 ký tự")
    private String description;
    private Long categoryId;

    @NotNull(message = "Trọng lượng sản phẩm là bắt buộc")
    @Min(value = 0, message = "Trọng lượng phải >= 0 (gram)")
    private Long weight;
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
