package com.chobbi.server.catalog.controller;

import com.chobbi.server.catalog.dto.CreateProductRequest;
import com.chobbi.server.catalog.services.ProductServices;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductServices productServices;

//    @GetMapping(params = {"shopId", "productId"})
//    public ResponseEntity<?> getProduct(@RequestParam Long shopId, @RequestParam Long productId) {
//        return ResponseEntity.ok(productServices.getProduct(shopId, productId));
//    }
//    @GetMapping(params = "shopId")
//    public ResponseEntity<?> getProducts(@RequestParam Long shopId) {
//        return ResponseEntity.ok(productServices.getProducts(shopId));
//    }
    @PostMapping("/create")
    public ResponseEntity<?> createProduct(@RequestBody @Valid CreateProductRequest productRequest) {
        productServices.createProduct(productRequest);
        return ResponseEntity.ok("fdfdf");
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
