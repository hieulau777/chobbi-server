package com.chobbi.server.services;

import com.chobbi.server.dto.ProductCategoryDto;
import com.chobbi.server.entity.CategoryEntity;

import java.util.List;

public interface CategoryServices {
    List<ProductCategoryDto> getBreadcrumb(Long id);
}
