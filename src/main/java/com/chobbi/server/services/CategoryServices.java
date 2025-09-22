package com.chobbi.server.services;

import com.chobbi.server.entity.CategoryEntity;

import java.util.List;

public interface CategoryServices {
    List<CategoryEntity> getBreadcrumb(Long id);
}
