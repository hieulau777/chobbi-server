package com.chobbi.server.shop.controller;

import com.chobbi.server.auth.AccountPrincipal;
import com.chobbi.server.shop.dto.ShopResponse;
import com.chobbi.server.shop.services.ShopServices;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/shop")
@RequiredArgsConstructor
public class ShopController {

    private final ShopServices shopServices;

    @GetMapping("/me")
    public ResponseEntity<ShopResponse> getMyShop(
            @AuthenticationPrincipal AccountPrincipal principal
    ) {
        Optional<ShopResponse> shopOpt = shopServices.getMyShop(principal.getAccountId());
        return shopOpt
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ShopResponse> createShop(
            @AuthenticationPrincipal AccountPrincipal principal,
            @RequestBody CreateShopRequest request
    ) {
        ShopResponse response = shopServices.createShop(principal.getAccountId(), request.getName());
        return ResponseEntity.ok(response);
    }

    @Getter
    @Setter
    private static class CreateShopRequest {
        @NotBlank
        private String name;
    }
}

