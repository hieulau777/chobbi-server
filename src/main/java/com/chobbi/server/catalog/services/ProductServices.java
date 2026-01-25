package com.chobbi.server.catalog.services;

import com.chobbi.server.catalog.dto.CategoryDto;
import com.chobbi.server.catalog.dto.CreateProductRequest;
import com.chobbi.server.catalog.dto.ReadProductDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductServices {
    void createProduct(CreateProductRequest productRequest, MultipartFile[] media);
    ReadProductDto readProduct(Long productId);
}
