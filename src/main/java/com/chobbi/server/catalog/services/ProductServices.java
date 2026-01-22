package com.chobbi.server.catalog.services;

import com.chobbi.server.catalog.dto.CreateProductRequest;
import org.springframework.web.multipart.MultipartFile;

public interface ProductServices {
    void createProduct(CreateProductRequest productRequest, MultipartFile[] media);
}
