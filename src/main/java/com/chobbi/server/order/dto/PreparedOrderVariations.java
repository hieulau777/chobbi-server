package com.chobbi.server.order.dto;

import com.chobbi.server.catalog.entity.VariationEntity;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
public class PreparedOrderVariations {
    private VariationEntity variation;
    private Integer quantity;
    private BigDecimal priceAtPoint;
}
