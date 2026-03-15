package com.chobbi.server.auth.controller;

import com.chobbi.server.auth.services.EmailBlacklistService;
import com.chobbi.server.catalog.entity.ProductEntity;
import com.chobbi.server.catalog.enums.StatusEnums;
import com.chobbi.server.catalog.repo.ProductRepo;
import com.chobbi.server.shop.repo.ShopRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Admin API chuẩn để quản lý email blacklist.
 * Được bảo vệ bởi AdminPasswordFilter (/admin/** + X-Admin-Email + X-Admin-Pwd).
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/blacklist")
public class BlacklistController {

    private final EmailBlacklistService emailBlacklistService;
    private final ShopRepo shopRepo;
    private final ProductRepo productRepo;

    private void updateShopStatusForEmail(String email, StatusEnums status) {
        if (email == null || email.isBlank()) {
            return;
        }
        shopRepo.findByAccountEntity_EmailAndDeletedAtIsNull(email.trim().toLowerCase())
                        .ifPresent(shop -> {
                            shop.setStatus(status);
                            shopRepo.save(shop);

                            // Cập nhật luôn status của tất cả sản phẩm thuộc shop này.
                            // Chỉ chuyển ACTIVE -> INACTIVE khi blacklist, và INACTIVE -> ACTIVE khi gỡ blacklist.
                            for (ProductEntity p : productRepo.findAllByShopEntity_IdAndDeletedAtIsNull(shop.getId())) {
                                if (status == StatusEnums.INACTIVE && p.getStatus() == StatusEnums.ACTIVE) {
                                    p.setStatus(StatusEnums.INACTIVE);
                                } else if (status == StatusEnums.ACTIVE && p.getStatus() == StatusEnums.INACTIVE) {
                                    p.setStatus(StatusEnums.ACTIVE);
                                }
                            }
                            // Lưu lại thay đổi product
                            // (nếu không có sản phẩm nào, saveAll sẽ chỉ là no-op).
                            productRepo.flush();
                        });
    }

    @PostMapping("/add")
    public ResponseEntity<?> addToBlacklist(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        emailBlacklistService.addToBlacklist(email);
        // Khi email bị blacklist, nếu user có shop thì set shop sang trạng thái INACTIVE.
        updateShopStatusForEmail(email, StatusEnums.INACTIVE);
        return ResponseEntity.ok().body(Map.of(
                "email", email,
                "status", "added"
        ));
    }

    @PostMapping("/remove")
    public ResponseEntity<?> removeFromBlacklist(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        emailBlacklistService.removeFromBlacklist(email);
        // Khi gỡ blacklist, nếu user có shop thì set shop về ACTIVE để hiển thị lại.
        updateShopStatusForEmail(email, StatusEnums.ACTIVE);
        return ResponseEntity.ok().body(Map.of(
                "email", email,
                "status", "removed"
        ));
    }

    @PostMapping("/list")
    public ResponseEntity<?> listBlacklist() {
        return ResponseEntity.ok(Map.of(
                "emails", emailBlacklistService.getAllBlacklistedEmails()
        ));
    }
}

