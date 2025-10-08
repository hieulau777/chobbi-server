package com.chobbi.server.services;

import com.chobbi.server.dto.CartAddDto;
import com.chobbi.server.dto.CartDto;
import com.chobbi.server.payload.request.AddToCartRequest;

import java.util.List;

public interface CartServices {
    List<CartAddDto> addToCart(AddToCartRequest req);
    List<CartDto> getCart(Long accountId);
}
