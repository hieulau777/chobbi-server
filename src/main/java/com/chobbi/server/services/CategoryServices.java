package com.chobbi.server.services;

import com.chobbi.server.dto.CategoryDto;
import com.chobbi.server.entity.CategoryEntity;

import java.util.List;

public interface CategoryServices {
    List<CategoryDto> getBreadcrumb(Long id);
    CategoryEntity getLeafCategoryOrThrow(Long categoryId);
}
