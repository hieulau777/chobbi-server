package com.chobbi.server.cart.services;

import com.chobbi.server.cart.dto.AddCartRequest;
import com.chobbi.server.cart.dto.GetCartResponseDto;

public interface CartServices {
    void addToCart(AddCartRequest req);

    GetCartResponseDto getCartByAccountId(Long accountId);
}
