package com.chobbi.server.cart.services;

import com.chobbi.server.cart.dto.AddCartRequest;

public interface CartServices {
    void addToCart(AddCartRequest req);
}
