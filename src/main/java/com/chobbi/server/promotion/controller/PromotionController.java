package com.chobbi.server.promotion.controller;

import com.chobbi.server.auth.AccountPrincipal;
import com.chobbi.server.promotion.dto.CreatePromotionRequest;
import com.chobbi.server.promotion.dto.ShopPromotionResponse;
import com.chobbi.server.promotion.services.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/promotion")
@RequiredArgsConstructor
public class PromotionController {

    private final PromotionService promotionService;

    @PostMapping("/seller")
    public ResponseEntity<Void> createPromotion(
            @AuthenticationPrincipal AccountPrincipal principal,
            @RequestBody CreatePromotionRequest request
    ) {
        promotionService.createPromotionWithProducts(principal.getAccountId(), request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/seller/{promotionId}")
    public ResponseEntity<Void> updatePromotion(
            @AuthenticationPrincipal AccountPrincipal principal,
            @PathVariable Long promotionId,
            @RequestBody CreatePromotionRequest request
    ) {
        promotionService.updatePromotionWithProducts(principal.getAccountId(), promotionId, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/seller")
    public ResponseEntity<List<ShopPromotionResponse>> listMyPromotions(
            @AuthenticationPrincipal AccountPrincipal principal
    ) {
        List<ShopPromotionResponse> promotions = promotionService.listMyPromotions(principal.getAccountId());
        return ResponseEntity.ok(promotions);
    }
}

