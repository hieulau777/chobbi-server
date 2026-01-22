package com.chobbi.server.catalog.controller;

import com.chobbi.server.catalog.dto.CreateProductRequest;
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
//    public ResponseEntity<?> getProduct(@RequestParam Long shopId, @RequestParam Long productId) {
//        return ResponseEntity.ok(productServices.getProduct(shopId, productId));
//    }

    @PostMapping(
            value = "/create",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> createProduct(
            @RequestPart("product") @Valid CreateProductRequest productRequest,
            @RequestPart(value = "images", required = false) MultipartFile[] media
    ) {
        productServices.createProduct(productRequest, media);
        return ResponseEntity.ok("ok");
    }
    @GetMapping(params = "productId")
    public ResponseEntity<?> getProduct(@RequestParam Long productId) {
        return ResponseEntity.ok("ffff");
    }
//    @PostMapping("/update")
//    public ResponseEntity<?> updateProduct(@RequestBody ProductRequest productRequest) {
//        return ResponseEntity.ok(productServices.updateProduct(productRequest));
//    }
//    @PostMapping("/delete")
//    public void deleteProduct(@RequestParam Long shopId, @RequestParam Long productId) {
//        productServices.deleteProduct(shopId, productId);
//    }
}
