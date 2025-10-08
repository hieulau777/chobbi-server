package com.chobbi.server.services;

import com.chobbi.server.dto.ProductDto;
import com.chobbi.server.payload.request.ProductRequest;

import java.util.List;

public interface ProductServices {
    ProductDto getProduct(Long shopId, Long productId);
    List<ProductDto> getProducts(Long shopId);
    ProductDto createProduct(ProductRequest productRequest);
    ProductDto updateProduct(ProductRequest request);
    void deleteProduct(Long shopId, Long productId);
}
