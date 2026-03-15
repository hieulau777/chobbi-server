package com.chobbi.server.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Trang danh sách sản phẩm public của shop (cho trang xem shop client).
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PublicShopProductListPageDto {
    private List<PublicShopProductDto> content;
    private int totalPages;
    private long totalElements;
    /** Trang hiện tại (0-based). */
    private int number;
    private int size;
}

