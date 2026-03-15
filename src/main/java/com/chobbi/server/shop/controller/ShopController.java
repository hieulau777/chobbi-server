package com.chobbi.server.shop.controller;

import com.chobbi.server.auth.AccountPrincipal;
import com.chobbi.server.shop.dto.CreateShopCategoryRequest;
import com.chobbi.server.shop.dto.PublicShopPageDto;
import com.chobbi.server.shop.dto.PublicShopProductListPageDto;
import com.chobbi.server.shop.dto.ShopBannerDto;
import com.chobbi.server.shop.dto.ShopCategoryResponse;
import com.chobbi.server.shop.dto.ShopResponse;
import com.chobbi.server.shop.services.ShopServices;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/shop")
@RequiredArgsConstructor
public class ShopController {

    private final ShopServices shopServices;

    /** API public (không cần đăng nhập): thông tin shop + danh mục + sản phẩm. */
    @GetMapping("/public/{shopId}")
    public ResponseEntity<PublicShopPageDto> getPublicShopPage(@PathVariable Long shopId) {
        PublicShopPageDto dto = shopServices.getPublicShopPage(shopId);
        return ResponseEntity.ok(dto);
    }

    /**
     * API public (không cần đăng nhập): danh sách sản phẩm ACTIVE của shop,
     * có thể lọc theo danh mục shop con (shopCategoryId) hoặc lấy tất cả.
     * Phân trang: page (0-based), size.
     */
    @GetMapping("/public/{shopId}/products")
    public ResponseEntity<PublicShopProductListPageDto> getPublicShopProducts(
            @PathVariable Long shopId,
            @RequestParam(required = false) Long shopCategoryId,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "3") Integer size
    ) {
        int p = page != null && page >= 0 ? page : 0;
        int s = size != null && size > 0 ? size : 3;
        return ResponseEntity.ok(
                shopServices.getPublicShopProducts(shopId, shopCategoryId, p, s)
        );
    }

    @GetMapping("/me")
    public ResponseEntity<ShopResponse> getMyShop(
            @AuthenticationPrincipal AccountPrincipal principal
    ) {
        Optional<ShopResponse> shopOpt = shopServices.getMyShop(principal.getAccountId());
        return shopOpt
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping(value = "/me", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ShopResponse> updateShopInfo(
            @AuthenticationPrincipal AccountPrincipal principal,
            @RequestPart(value = "name", required = false) String name,
            @RequestPart(value = "avatar", required = false) MultipartFile avatar
    ) {
        ShopResponse response = shopServices.updateShopInfo(principal.getAccountId(), name, avatar);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ShopResponse> createShop(
            @AuthenticationPrincipal AccountPrincipal principal,
            @RequestBody CreateShopRequest request
    ) {
        ShopResponse response = shopServices.createShop(principal.getAccountId(), request.getName());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/categories")
    public ResponseEntity<ShopCategoryResponse> createShopCategory(
            @AuthenticationPrincipal AccountPrincipal principal,
            @RequestBody CreateShopCategoryRequest request
    ) {
        ShopCategoryResponse response = shopServices.createShopCategory(principal.getAccountId(), request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/categories")
    public ResponseEntity<List<ShopCategoryResponse>> listMyShopCategories(
            @AuthenticationPrincipal AccountPrincipal principal
    ) {
        List<ShopCategoryResponse> responses = shopServices.listMyShopCategories(principal.getAccountId());
        return ResponseEntity.ok(responses);
    }

    public record UpdateCategoryStatusRequest(Long id, Boolean isActive) {}

    @PostMapping("/categories/status")
    public ResponseEntity<ShopCategoryResponse> updateShopCategoryStatus(
            @AuthenticationPrincipal AccountPrincipal principal,
            @RequestBody UpdateCategoryStatusRequest request
    ) {
        if (request.id() == null || request.isActive() == null) {
            return ResponseEntity.badRequest().build();
        }
        ShopCategoryResponse response = shopServices.updateShopCategoryStatus(
                principal.getAccountId(),
                request.id(),
                request.isActive()
        );
        return ResponseEntity.ok(response);
    }

    /** Full update danh sách sản phẩm thuộc danh mục shop. Body: list productId. */
    @PutMapping("/categories/{categoryId}/products")
    public ResponseEntity<ShopCategoryResponse> updateShopCategoryProducts(
            @AuthenticationPrincipal AccountPrincipal principal,
            @PathVariable Long categoryId,
            @RequestBody List<Long> productIds
    ) {
        ShopCategoryResponse response = shopServices.updateShopCategoryProducts(
                principal.getAccountId(),
                categoryId,
                productIds != null ? productIds : List.of()
        );
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/categories/{categoryId}")
    public ResponseEntity<ShopCategoryResponse> updateShopCategoryName(
            @AuthenticationPrincipal AccountPrincipal principal,
            @PathVariable Long categoryId,
            @RequestBody UpdateCategoryNameRequest request
    ) {
        if (request == null || request.name() == null || request.name().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        ShopCategoryResponse response = shopServices.updateShopCategoryName(
                principal.getAccountId(),
                categoryId,
                request.name().trim()
        );
        return ResponseEntity.ok(response);
    }

    public record UpdateCategoryNameRequest(String name) {}

    /** Body: danh sách categoryId theo thứ tự mong muốn (index 0 = sortOrder 0). */
    @PutMapping("/categories/order")
    public ResponseEntity<List<ShopCategoryResponse>> updateShopCategoryOrder(
            @AuthenticationPrincipal AccountPrincipal principal,
            @RequestBody List<Long> categoryIdsInOrder
    ) {
        List<ShopCategoryResponse> responses = shopServices.updateShopCategoryOrder(
                principal.getAccountId(),
                categoryIdsInOrder != null ? categoryIdsInOrder : List.of()
        );
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/banners")
    public ResponseEntity<List<ShopBannerDto>> listBanners(
            @AuthenticationPrincipal AccountPrincipal principal
    ) {
        List<ShopBannerDto> list = shopServices.listBanners(principal.getAccountId());
        return ResponseEntity.ok(list);
    }

    @PostMapping(value = "/banners", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ShopBannerDto> uploadBanner(
            @AuthenticationPrincipal AccountPrincipal principal,
            @RequestPart("file") MultipartFile file
    ) {
        ShopBannerDto dto = shopServices.addBanner(principal.getAccountId(), file);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/banners/{bannerId}")
    public ResponseEntity<Void> deleteBanner(
            @AuthenticationPrincipal AccountPrincipal principal,
            @PathVariable Long bannerId
    ) {
        shopServices.deleteBanner(principal.getAccountId(), bannerId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/banners/order")
    public ResponseEntity<List<ShopBannerDto>> updateBannerOrder(
            @AuthenticationPrincipal AccountPrincipal principal,
            @RequestBody List<Long> bannerIdsInOrder
    ) {
        List<ShopBannerDto> list = shopServices.updateBannerOrder(
                principal.getAccountId(),
                bannerIdsInOrder != null ? bannerIdsInOrder : List.of()
        );
        return ResponseEntity.ok(list);
    }

    @Getter
    @Setter
    private static class CreateShopRequest {
        @NotBlank
        private String name;
    }
}

