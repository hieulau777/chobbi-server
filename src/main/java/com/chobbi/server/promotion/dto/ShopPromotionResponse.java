package com.chobbi.server.promotion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ShopPromotionResponse {
    private Long id;
    private Long shopId;
    private String name;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    /** Đang trong thời gian hiệu lực hay không (so với thời điểm hiện tại). */
    private boolean active;
    /** Danh sách sản phẩm thuộc promotion. */
    private List<ShopPromotionProductDto> products;
}

