package com.chobbi.server.controller;

import com.chobbi.server.services.ProductServices;
import com.chobbi.server.services.imp.ProductServicesImp;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    @GetMapping("/hello")
    public ResponseEntity<?> getHello() {
        return ResponseEntity.ok("Hello World!");
    }
}
