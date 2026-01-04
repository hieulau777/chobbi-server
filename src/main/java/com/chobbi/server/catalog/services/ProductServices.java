package com.chobbi.server.catalog.services;

import com.chobbi.server.catalog.dto.CreateProductRequest;

import java.util.List;

public interface ProductServices {
    void createProduct(CreateProductRequest req);
}
