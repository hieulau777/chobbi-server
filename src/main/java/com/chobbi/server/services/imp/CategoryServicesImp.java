package com.chobbi.server.services.imp;

import com.chobbi.server.dto.CategoryDto;
import com.chobbi.server.entity.CategoryEntity;
import com.chobbi.server.repo.CategoryRepo;
import com.chobbi.server.services.CategoryServices;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServicesImp implements CategoryServices {

    private final CategoryRepo categoryRepo;

    @Override
    public List<CategoryDto> getBreadcrumb(Long leafCategoryId) {
        List<CategoryDto> breadcrumb = new ArrayList<>();
        CategoryEntity category = categoryRepo.findById(leafCategoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        while (category != null) {
            breadcrumb.add(0, new CategoryDto(category.getId(), category.getName())); // thêm vào đầu list
            if (category.getParentId() == null) break;
            category = categoryRepo.findById(category.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent category not found"));
        }

        return breadcrumb;
    }

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
