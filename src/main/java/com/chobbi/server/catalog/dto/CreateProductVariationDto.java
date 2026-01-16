package com.chobbi.server.catalog.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductVariationDto {
    private BigDecimal price;
    private Integer stock;
    private String img;
    @Valid
    @Size(max = 2)
    private List<CreateProductRequestOptionCombination> optionCombination;
}
