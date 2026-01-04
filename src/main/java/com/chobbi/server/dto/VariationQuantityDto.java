package com.chobbi.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VariationQuantityDto {
    private Long variationId;
    private Long variationName;
    private BigDecimal price;
    private BigDecimal priceDiscount;
    private Integer stock;
    private Integer quantity;
}
