package com.chobbi.server.catalog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProductTierDto {
    private Long id; // Có ID = update tên Tier; Không ID = thêm mới Tier [20]
    private String name;
    private Boolean hasImages;
    private List<ProductTierOptionDto> options;
}
