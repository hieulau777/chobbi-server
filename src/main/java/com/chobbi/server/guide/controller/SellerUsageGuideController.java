package com.chobbi.server.guide.controller;

import com.chobbi.server.auth.AccountPrincipal;
import com.chobbi.server.guide.dto.SellerSeedStatusDto;
import com.chobbi.server.guide.dto.SellerUsageGuideDto;
import com.chobbi.server.guide.services.SellerUsageGuideService;
import com.chobbi.server.guide.entity.SellerSeedHistoryDocument;
import com.chobbi.server.guide.repo.SellerSeedHistoryRepository;
import com.chobbi.server.catalog.dto.admin.AdminProductSeedRequest;
import com.chobbi.server.catalog.services.AdminCatalogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/guide/seller")
@RequiredArgsConstructor
public class SellerUsageGuideController {

    private final SellerUsageGuideService sellerUsageGuideService;
    private final AdminCatalogService adminCatalogService;
    private final ObjectMapper objectMapper;
    private final SellerSeedHistoryRepository sellerSeedHistoryRepository;

    @GetMapping
    public ResponseEntity<SellerUsageGuideDto> getSellerGuide() {
        return ResponseEntity.ok(sellerUsageGuideService.getSellerGuide());
    }

    @PostMapping("/seed-demo")
    public ResponseEntity<SellerSeedStatusDto> seedDemoProducts(
            @AuthenticationPrincipal AccountPrincipal principal
    ) {
        if (principal == null) {
            return ResponseEntity.status(401).body(
                    SellerSeedStatusDto.builder()
                            .success(false)
                            .message("Unauthenticated")
                            .build()
            );
        }

        try {
            // Nếu seller này đã seed demo trước đó thì không cho seed lại
            if (sellerSeedHistoryRepository.existsByAccountId(principal.getAccountId())) {
                return ResponseEntity.ok(
                        SellerSeedStatusDto.builder()
                                .success(false)
                                .message("Bạn đã seed sản phẩm demo trước đó. Vui lòng vào trang Tất cả sản phẩm để xem.")
                                .build()
                );
            }

            SellerUsageGuideDto guide = sellerUsageGuideService.getSellerGuide();
            String json = guide.getSeedConfigJson();
            if (json == null || json.isBlank()) {
                return ResponseEntity.badRequest().body(
                        SellerSeedStatusDto.builder()
                                .success(false)
                                .message("Chưa cấu hình seed JSON cho seller.")
                                .build()
                );
            }

            AdminProductSeedRequest request =
                    objectMapper.readValue(json, AdminProductSeedRequest.class);

            // Luôn dùng email seller hiện tại, bỏ qua email trong JSON nếu có
            request.setEmail(principal.getEmail());

            adminCatalogService.seedProducts(request);

            // Ghi lại lịch sử để không cho seed lại lần nữa
            SellerSeedHistoryDocument history = SellerSeedHistoryDocument.builder()
                    .accountId(principal.getAccountId())
                    .seededAt(java.time.Instant.now())
                    .build();
            sellerSeedHistoryRepository.save(history);

            return ResponseEntity.ok(
                    SellerSeedStatusDto.builder()
                            .success(true)
                            .message("Seed demo products thành công.")
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    SellerSeedStatusDto.builder()
                            .success(false)
                            .message(e.getMessage())
                            .build()
            );
        }
    }
}


