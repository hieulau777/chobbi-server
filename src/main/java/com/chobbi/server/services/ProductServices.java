package com.chobbi.server.services;

import com.chobbi.server.dto.ProductDto;
import com.chobbi.server.dto.ProductVariantDto;
import com.chobbi.server.entity.ProductVariantEntity;
import org.springframework.stereotype.Service;

import java.util.List;

public interface ProductServices {
    ProductDto getProduct(Long shopId, Long productId);
}
