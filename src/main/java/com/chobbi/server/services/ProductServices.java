package com.chobbi.server.services;

import com.chobbi.server.dto.ProductDto;

import java.util.List;

public interface ProductServices {
    ProductDto getProduct(Long shopId, Long productId);
    List<ProductDto> getProducts(Long shopId);
}
