package com.chobbi.server.guide.controller;

import com.chobbi.server.guide.dto.SellerUsageGuideDto;
import com.chobbi.server.guide.services.SellerUsageGuideService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/usage-guide/seller")
@RequiredArgsConstructor
public class AdminSellerUsageGuideController {

    private final SellerUsageGuideService sellerUsageGuideService;

    @GetMapping
    public ResponseEntity<SellerUsageGuideDto> getSellerGuide() {
        return ResponseEntity.ok(sellerUsageGuideService.getSellerGuide());
    }

    @PutMapping
    public ResponseEntity<SellerUsageGuideDto> upsertSellerGuide(
            @RequestBody SellerUsageGuideDto request
    ) {
        SellerUsageGuideDto saved = sellerUsageGuideService.upsertSellerGuide(request);
        return ResponseEntity.ok(saved);
    }
}

