package com.chobbi.server.catalog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReadProductVariationDto {
    private BigDecimal price;
    private Integer stock;
    private List<ReadProductVariationOptionDto> optionCombination;
}
