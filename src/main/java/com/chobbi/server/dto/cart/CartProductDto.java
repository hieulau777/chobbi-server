package com.chobbi.server.dto.cart;

import com.chobbi.server.dto.TierDto;
import com.chobbi.server.dto.VariationDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartProductDto {
    private Long productId;
    private String productName;
    private Integer quantity;
    private Long selected_variation_id;
    private BigDecimal price_unit;
    private List<TierDto> tiers = new ArrayList<>();
    private List<VariationDto> variations = new ArrayList<>();
}
