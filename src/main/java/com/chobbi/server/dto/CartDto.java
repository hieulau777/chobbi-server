package com.chobbi.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartDto {
    private Integer quantity;
    private Long productId;
    private String productName;
    private Long selected_variation_id;
    private List<TierDto> tiers = new ArrayList<>();
    private List<VariationDto> variations = new ArrayList<>();
}
