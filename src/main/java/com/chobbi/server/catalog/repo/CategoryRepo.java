package com.chobbi.server.catalog.repo;

import com.chobbi.server.catalog.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepo extends JpaRepository<CategoryEntity, Long> {
    boolean existsByParentId(Long parentId);
}
