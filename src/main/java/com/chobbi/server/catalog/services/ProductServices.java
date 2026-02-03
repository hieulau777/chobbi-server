package com.chobbi.server.catalog.services;

import com.chobbi.server.catalog.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductServices {
    void createProduct(ProductRequest productRequest, MultipartFile[] media);
    ReadProductDto readProduct(Long productId);
    void updateProduct(ProductRequest productRequest, MultipartFile[] media);
}
