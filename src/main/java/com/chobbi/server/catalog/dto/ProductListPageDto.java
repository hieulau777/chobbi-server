package com.chobbi.server.catalog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** Trang danh sách sản phẩm (seller): content + thông tin phân trang */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductListPageDto {
    private List<ReadProductSellerDto> content;
    private int totalPages;
    private long totalElements;
    /** Trang hiện tại (0-based) */
    private int number;
    private int size;
}
