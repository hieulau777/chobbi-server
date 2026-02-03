package com.chobbi.server.catalog.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class UpdateProductVariationDto {
    private Long id; // Có ID = update price/stock; Không ID = tạo mới [22]
    private BigDecimal price;
    private Integer stock;

    // Dùng để map khi tạo mới (Case 2)
    // Chứa thông tin để tìm hoặc tạo liên kết với OptionEntity [23, 24]
    private List<UpdateProductVariationCombinationDto> optionCombination;
}
