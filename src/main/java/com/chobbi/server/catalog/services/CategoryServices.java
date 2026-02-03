package com.chobbi.server.catalog.services;

import com.chobbi.server.catalog.dto.CategoryDto;
import com.chobbi.server.catalog.dto.ReadProductAttributes;
import com.chobbi.server.catalog.entity.CategoryEntity;

import java.util.List;

public interface CategoryServices {
    List<CategoryDto> getTree();
    List<CategoryDto> getTree(Long categoryId);
    CategoryEntity getLeafCategoryOrThrow(Long categoryId);
    List<ReadProductAttributes> getAttributes(Long categoryId);
}
