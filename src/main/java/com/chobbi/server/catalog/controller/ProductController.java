package com.chobbi.server.catalog.controller;

import com.chobbi.server.catalog.dto.ProductRequest;
import com.chobbi.server.catalog.services.ProductServices;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductServices productServices;

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
    public ResponseEntity<?> listProductsByShopId(@RequestParam Long shopId) {
        return ResponseEntity.ok(productServices.listProductsByShopId(shopId));
    }

    /** Danh sách sản phẩm thuộc category lá (cho trang category client). */
    @GetMapping(value = "/list", params = "categoryId")
    public ResponseEntity<?> listProductsByCategoryId(
            @RequestParam Long categoryId,
            @RequestParam(required = false) java.math.BigDecimal minPrice,
            @RequestParam(required = false) java.math.BigDecimal maxPrice,
            @RequestParam(required = false) java.util.List<Long> brandValueIds
    ) {
        return ResponseEntity.ok(productServices.listProductsByCategoryId(categoryId, minPrice, maxPrice, brandValueIds));
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
}
