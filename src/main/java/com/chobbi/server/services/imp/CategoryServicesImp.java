package com.chobbi.server.services.imp;

import com.chobbi.server.dto.ProductCategoryDto;
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
    public List<ProductCategoryDto> getBreadcrumb(Long leafCategoryId) {
        List<ProductCategoryDto> breadcrumb = new ArrayList<>();
        CategoryEntity category = categoryRepo.findById(leafCategoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        while (category != null) {
            breadcrumb.add(0, new ProductCategoryDto(category.getId(), category.getName())); // thêm vào đầu list
            if (category.getParentId() == null) break;
            category = categoryRepo.findById(category.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent category not found"));
        }

        return breadcrumb;
    }
}
