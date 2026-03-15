package com.chobbi.server.catalog.controller;

import com.chobbi.server.auth.AccountPrincipal;
import com.chobbi.server.catalog.dto.ProductCardListPageDto;
import com.chobbi.server.catalog.dto.ProductRequest;
import com.chobbi.server.catalog.dto.ProductStatusUpdateRequest;
import com.chobbi.server.catalog.services.ProductServices;
import com.chobbi.server.exception.BusinessException;
import com.chobbi.server.shop.repo.ShopRepo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductServices productServices;
    private final ShopRepo shopRepo;

//    @GetMapping(params = {"shopId", "productId"})
//    public ResponseEntity<?> readProduct(@RequestParam Long shopId, @RequestParam Long productId) {
//        return ResponseEntity.ok(productServices.readProduct(shopId, productId));
//    }

    @PostMapping(
            value = "/create",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> createProduct(
            @RequestPart("product") @Valid ProductRequest productRequest,
            @RequestPart(value = "media", required = false) MultipartFile[] media
    ) {
        productServices.createProduct(productRequest, media);
        return ResponseEntity.ok("ok");
    }

    @GetMapping("/{productId}")
    public ResponseEntity<?> readProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(productServices.readProduct(productId));
    }

    @GetMapping(value = "/list", params = "shopId")
    public ResponseEntity<?> listProductsByShopId(
            @RequestParam Long shopId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Long categoryId
    ) {
        return ResponseEntity.ok(
                productServices.listProductsByShopId(shopId, name, minPrice, maxPrice, categoryId)
        );
    }

    /**
     * Danh sách sản phẩm của shop của seller đang đăng nhập (dùng account từ JWT).
     * Chỉ SELLER có quyền; shop lấy theo 1 user 1 shop.
     * Phân trang: page (0-based), size (mặc định 3). status: ACTIVE | DRAFT (trống = tất cả).
     */
    @GetMapping("/seller/list")
    public ResponseEntity<?> listMyShopProducts(
            @AuthenticationPrincipal AccountPrincipal principal,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sortField,
            @RequestParam(required = false) String sortDir
    ) {
        Long shopId = shopRepo.findByAccountEntity_IdAndDeletedAtIsNull(principal.getAccountId())
                .map(s -> s.getId())
                .orElseThrow(() -> new BusinessException("Bạn chưa có shop", HttpStatus.BAD_REQUEST));
        return ResponseEntity.ok(
                productServices.listProductsByShopId(
                        shopId,
                        name,
                        minPrice,
                        maxPrice,
                        categoryId,
                        status,
                        page,
                        size,
                        sortField,
                        sortDir
                )
        );
    }

    /**
     * Số lượng sản phẩm theo trạng thái (all, active, draft) cho tab.
     */
    @GetMapping("/seller/counts")
    public ResponseEntity<?> getMyShopProductCounts(@AuthenticationPrincipal AccountPrincipal principal) {
        Long shopId = shopRepo.findByAccountEntity_IdAndDeletedAtIsNull(principal.getAccountId())
                .map(s -> s.getId())
                .orElseThrow(() -> new BusinessException("Bạn chưa có shop", HttpStatus.BAD_REQUEST));
        return ResponseEntity.ok(productServices.getProductCountsByShop(shopId));
    }

    /** Danh sách sản phẩm thuộc category (cho trang category client). Phân trang: page (0-based), size (mặc định 12). */
    @GetMapping(value = "/list", params = "categoryId")
    public ResponseEntity<ProductCardListPageDto> listProductsByCategoryId(
            @RequestParam Long categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) java.util.List<Long> brandValueIds,
            @RequestParam(required = false) String sortField,
            @RequestParam(required = false) String sortDir,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "12") Integer size
    ) {
        int p = page != null && page >= 0 ? page : 0;
        int s = size != null && size > 0 ? size : 12;
        return ResponseEntity.ok(
                productServices.listProductsByCategoryIdPaged(categoryId, minPrice, maxPrice, brandValueIds, sortField, sortDir, p, s)
        );
    }

    /**
     * Search đơn giản theo tên sản phẩm + filter khoảng giá (cho trang search client).
     */
    @GetMapping(value = "/search", params = "q")
    public ResponseEntity<?> searchProductsByName(
            @RequestParam("q") String keyword,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String sortField,
            @RequestParam(required = false) String sortDir
    ) {
        return ResponseEntity.ok(productServices.searchProductsByName(keyword, minPrice, maxPrice, sortField, sortDir));
    }

    @GetMapping("/client")
    public ResponseEntity<?> getProductDetailClient(@RequestParam Long productId) {
        return ResponseEntity.ok(productServices.getProductDetailClient(productId));
    }

    /** Danh sách brand (attribute "Thương hiệu") cho nhánh category. */
    @GetMapping("/brands")
    public ResponseEntity<?> listBrandsForCategoryBranch(@RequestParam Long categoryId) {
        return ResponseEntity.ok(productServices.listBrandsForCategoryBranch(categoryId));
    }

    @PostMapping(
            value = "/update",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> updateProduct(
            @RequestPart("product") @Valid ProductRequest productRequest,
            @RequestPart(value = "media", required = false) MultipartFile[] media
    ) {
        productServices.updateProduct(productRequest, media);
        return ResponseEntity.ok("ok");
    }

    /**
     * Xóa mềm một hoặc nhiều sản phẩm thuộc shop của seller đang đăng nhập.
     * Body: danh sách productId (List<Long>).
     *
     * Lưu ý: Dùng cho cả các trường hợp client gửi 1 id duy nhất trong list.
     */
    @DeleteMapping("/seller")
    public ResponseEntity<Void> deleteMyProducts(
            @AuthenticationPrincipal AccountPrincipal principal,
            @RequestBody java.util.List<Long> productIds
    ) {
        if (productIds == null || productIds.isEmpty()) {
            throw new BusinessException("Danh sách sản phẩm cần xóa không được để trống", HttpStatus.BAD_REQUEST);
        }
        Long accountId = principal.getAccountId();
        for (Long id : productIds) {
            if (id != null) {
                productServices.deleteMyProduct(accountId, id);
            }
        }
        return ResponseEntity.noContent().build();
    }

    /**
     * Cập nhật trạng thái (status) của một sản phẩm thuộc shop của seller đang đăng nhập.
     * Body: { productId, status } với status: ACTIVE | DRAFT | INACTIVE.
     */
    @PatchMapping("/seller/status")
    public ResponseEntity<Void> updateMyProductStatus(
            @AuthenticationPrincipal AccountPrincipal principal,
            @RequestBody ProductStatusUpdateRequest request
    ) {
        if (request == null || request.getProductId() == null) {
            throw new BusinessException("Thiếu productId", HttpStatus.BAD_REQUEST);
        }
        productServices.updateMyProductStatus(principal.getAccountId(), request.getProductId(), request.getStatus());
        return ResponseEntity.noContent().build();
    }
}
