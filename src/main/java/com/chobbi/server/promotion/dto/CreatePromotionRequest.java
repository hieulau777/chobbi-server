package com.chobbi.server.promotion.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class CreatePromotionRequest {

    private Long shopId;

    private String name;

    /**
     * Thời gian bắt đầu campaign.
     */
    private LocalDateTime startAt;

    /**
     * Thời gian kết thúc campaign.
     */
    private LocalDateTime endAt;

    /**
     * Danh sách productId tham gia campaign.
     */
    private List<Long> productIds;
}

