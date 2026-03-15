package com.chobbi.server.catalog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** Trang danh sách ProductCard (client: trang category, listing). */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductCardListPageDto {
    private List<ProductCardDto> content;
    private int totalPages;
    private long totalElements;
    /** Trang hiện tại (0-based). */
    private int number;
    private int size;
}
