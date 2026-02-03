package com.chobbi.server.catalog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductTierDto {
    private Long id;
    @NotBlank
    private String name;
    @NotEmpty
    private List<ProductTierOptionDto> options;
    @NotNull
    private Boolean hasImages;
}
