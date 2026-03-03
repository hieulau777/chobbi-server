package com.chobbi.server.cart.controller;

import com.chobbi.server.auth.AccountPrincipal;
import com.chobbi.server.cart.dto.AddCartRequest;
import com.chobbi.server.cart.dto.GetCartResponseDto;
import com.chobbi.server.cart.services.CartServices;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartServices cartServices;

    @PostMapping("add")
    public ResponseEntity<?> addCart(
            @AuthenticationPrincipal AccountPrincipal principal,
            @RequestBody AddCartRequest req) {
        req.setAccountId(principal.getAccountId());
        cartServices.addToCart(req);
        return ResponseEntity.ok("ok");
    }

    @GetMapping
    public ResponseEntity<GetCartResponseDto> getCart(@AuthenticationPrincipal AccountPrincipal principal) {
        GetCartResponseDto cart = cartServices.getCartByAccountId(principal.getAccountId());
        return ResponseEntity.ok(cart);
    }
}
