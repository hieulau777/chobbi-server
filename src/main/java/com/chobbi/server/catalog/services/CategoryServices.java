package com.chobbi.server.catalog.services;

import com.chobbi.server.catalog.entity.CategoryEntity;

public interface CategoryServices {
    CategoryEntity getLeafCategoryOrThrow(Long categoryId);
}
