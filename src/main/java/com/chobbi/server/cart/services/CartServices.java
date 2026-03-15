package com.chobbi.server.cart.services;

import com.chobbi.server.cart.dto.AddCartRequest;
import com.chobbi.server.cart.dto.GetCartResponseDto;
import com.chobbi.server.cart.dto.MiniCartResponseDto;

public interface CartServices {
    void addToCart(AddCartRequest req);

    GetCartResponseDto getCartByAccountId(Long accountId);

    /**
     * Mini cart cho header: tối đa 3 sản phẩm + tổng số item.
     */
    MiniCartResponseDto getMiniCartByAccountId(Long accountId);

    /**
     * Xóa mềm 1 item trong giỏ hàng (cart_variation) của account hiện tại.
     */
    void removeCartItem(Long accountId, Long cartVariationId);
}
