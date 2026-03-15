package com.chobbi.server.cart.controller;

import com.chobbi.server.auth.AccountPrincipal;
import com.chobbi.server.cart.dto.AddCartRequest;
import com.chobbi.server.cart.dto.GetCartResponseDto;
import com.chobbi.server.cart.dto.MiniCartResponseDto;
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

    /**
     * Mini cart cho header: trả về tối đa 3 sản phẩm + tổng số item trong giỏ.
     */
    @GetMapping("/mini")
    public ResponseEntity<MiniCartResponseDto> getMiniCart(@AuthenticationPrincipal AccountPrincipal principal) {
        MiniCartResponseDto mini = cartServices.getMiniCartByAccountId(principal.getAccountId());
        return ResponseEntity.ok(mini);
    }

    /**
     * Xóa mềm 1 item trong giỏ hàng của account hiện tại.
     */
    @DeleteMapping("/item/{cartVariationId}")
    public ResponseEntity<Void> removeCartItem(
            @AuthenticationPrincipal AccountPrincipal principal,
            @PathVariable Long cartVariationId
    ) {
        cartServices.removeCartItem(principal.getAccountId(), cartVariationId);
        return ResponseEntity.noContent().build();
    }
}
