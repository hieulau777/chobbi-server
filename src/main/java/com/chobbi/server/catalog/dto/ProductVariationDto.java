package com.chobbi.server.catalog.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductVariationDto {
    private Long id;
    private BigDecimal price;
    private Integer stock;
    @Valid
    @Size(max = 2)
    private List<ProductRequestOptionCombination> optionCombination;
}
