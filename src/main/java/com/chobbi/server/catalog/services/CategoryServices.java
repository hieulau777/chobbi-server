package com.chobbi.server.catalog.services;

import com.chobbi.server.catalog.dto.CategoryDto;
import com.chobbi.server.catalog.entity.CategoryEntity;

import java.util.List;

public interface CategoryServices {
    // Overloading 1: Lấy toàn bộ cây
    List<CategoryDto> getTree();

    // Overloading 2: Lấy cây của một nhánh nhất định
    List<CategoryDto> getTree(Long categoryId);

    // Kiểm tra Leaf node và ném lỗi với message tùy chỉnh
    CategoryEntity getLeafCategoryOrThrow(Long categoryId);
}
