package com.chobbi.server.repo;

import com.chobbi.server.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepo extends JpaRepository<CategoryEntity, Long> {
    boolean existsByParentId(Long parentId);
}
