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
public class VariationRequestDto {
    private Long id;
    private String name;
    private String sku;
    private BigDecimal price;
    private Long stock;
    private List<Integer> option_indices = new ArrayList<>();
}
