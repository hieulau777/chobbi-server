package com.chobbi.server.promotion.services;

import com.chobbi.server.promotion.dto.CreatePromotionRequest;
import com.chobbi.server.promotion.dto.ShopPromotionResponse;

import java.util.List;

public interface PromotionService {

    void createPromotionWithProducts(Long accountId, CreatePromotionRequest request);

    void updatePromotionWithProducts(Long accountId, Long promotionId, CreatePromotionRequest request);

    /**
     * Lấy tất cả promotion của shop hiện tại (theo account), kèm danh sách sản phẩm mỗi campaign.
     */
    List<ShopPromotionResponse> listMyPromotions(Long accountId);
}

