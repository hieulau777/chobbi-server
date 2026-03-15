package com.chobbi.server.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MiniCartResponseDto {
    /** Tối đa 3 sản phẩm đầu tiên trong giỏ. */
    private List<MiniCartItemDto> items;
    /** Tổng số dòng (item) trong giỏ (bao gồm cả những cái không hiển thị trong items). */
    private int totalItems;
}

