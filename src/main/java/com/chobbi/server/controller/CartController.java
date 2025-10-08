package com.chobbi.server.controller;

import com.chobbi.server.payload.request.AddToCartRequest;
import com.chobbi.server.services.CartServices;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartServices cartServices;
    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@RequestBody AddToCartRequest addToCartRequest) {
        return ResponseEntity.ok(cartServices.addToCart(addToCartRequest));
    }
    @PostMapping
    public ResponseEntity<?> getCart(@RequestParam Long accountId) {
        return ResponseEntity.ok(cartServices.getCart(accountId));
    }
}
