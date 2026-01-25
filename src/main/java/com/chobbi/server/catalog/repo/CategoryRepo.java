package com.chobbi.server.catalog.repo;

import com.chobbi.server.catalog.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryRepo extends JpaRepository<CategoryEntity, Long> {
    List<CategoryEntity> findAllByDeletedAtIsNull();

    boolean existsByParentId(Long id);

    // Query đệ quy để lấy chính nó và tất cả các con cháu bên dưới
    @Query(value = "WITH RECURSIVE cat_tree AS (" +
            "SELECT * FROM category WHERE id = :id " +
            "UNION ALL " +
            "SELECT c.* FROM category c INNER JOIN cat_tree ct ON c.parent_id = ct.id) " +
            "SELECT * FROM cat_tree", nativeQuery = true)
    List<CategoryEntity> findAllDescendants(@Param("id") Long id);
}
