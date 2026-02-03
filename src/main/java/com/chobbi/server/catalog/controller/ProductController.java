package com.chobbi.server.catalog.controller;

import com.chobbi.server.catalog.dto.CreateProductRequest;
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

    @GetMapping(params = "productId")
    public ResponseEntity<?> readProduct(@RequestParam Long productId) {
        return ResponseEntity.ok(productServices.readProduct(productId));
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
