package com.chobbi.server.catalog.services;

import com.chobbi.server.catalog.dto.CategoryDto;
import com.chobbi.server.catalog.dto.ReadProductAttributes;
import com.chobbi.server.catalog.entity.CategoryEntity;

import java.util.List;

public interface CategoryServices {
    List<CategoryDto> getTree();
    List<CategoryDto> getTree(Long categoryId);
    CategoryDto getBranchOfCategory(Long categoryId);
    /** Danh sách tất cả category lá (không có con), dùng cho trang chủ / filter. */
    List<CategoryDto> getLeafCategories();
    CategoryEntity getLeafCategoryOrThrow(Long categoryId);
    /** Lấy thông tin category theo id (cho trang category client). */
    CategoryDto getById(Long categoryId);
    List<ReadProductAttributes> getAttributes(Long categoryId);
}
