package com.chobbi.server.controller;

import com.chobbi.server.payload.request.ProductRequest;
import com.chobbi.server.services.ProductServices;
import com.chobbi.server.services.imp.ProductServicesImp;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductServices productServices;
    @GetMapping(params = {"shopId", "productId"})
    public ResponseEntity<?> getProduct(@RequestParam Long shopId, @RequestParam Long productId) {
        return ResponseEntity.ok(productServices.getProduct(shopId, productId));
    }
    @GetMapping(params = "shopId")
    public ResponseEntity<?> getProducts(@RequestParam Long shopId) {
        return ResponseEntity.ok(productServices.getProducts(shopId));
    }
    @PostMapping("/create")
    public ResponseEntity<?> createProduct(@RequestBody ProductRequest productRequest) {
        return ResponseEntity.ok(productServices.createProduct(productRequest));
    }
    @PostMapping("/update")
    public ResponseEntity<?> updateProduct(@RequestBody ProductRequest productRequest) {
        return ResponseEntity.ok(productServices.updateProduct(productRequest));
    }
    @PostMapping("/delete")
    public void deleteProduct(@RequestParam Long shopId, @RequestParam Long productId) {
        productServices.deleteProduct(shopId, productId);
    }
}
