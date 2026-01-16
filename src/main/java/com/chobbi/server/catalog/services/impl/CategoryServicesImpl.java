package com.chobbi.server.catalog.services.impl;

import com.chobbi.server.catalog.entity.CategoryEntity;
import com.chobbi.server.catalog.services.CategoryServices;
import com.chobbi.server.repo.CategoryRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryServicesImpl implements CategoryServices {
    private final CategoryRepo categoryRepo;

    @Override
    public CategoryEntity getLeafCategoryOrThrow(Long categoryId) {
        CategoryEntity category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        if (!isLeaf(category.getId())) {
            throw new RuntimeException("Category is not a leaf node");
        }
        return category;
    }
    /**
     * Kiểm tra category có phải là leaf không
     */
    private boolean isLeaf(Long categoryId) {
        return !categoryRepo.existsByParentId(categoryId);
    }
}
