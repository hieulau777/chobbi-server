package com.chobbi.server.shop.services;

import com.chobbi.server.shop.dto.ShopResponse;

import java.util.Optional;

public interface ShopServices {

    Optional<ShopResponse> getMyShop(Long accountId);

    ShopResponse createShop(Long accountId, String name);
}

