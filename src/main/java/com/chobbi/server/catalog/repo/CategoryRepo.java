package com.chobbi.server.catalog.repo;

import com.chobbi.server.catalog.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryRepo extends JpaRepository<CategoryEntity, Long> {
    List<CategoryEntity> findAllByDeletedAtIsNull();

    boolean existsByParentId(Long id);

    /**
     * Kiểm tra trùng tên cho root category (parent IS NULL).
     */
    boolean existsByNameIgnoreCaseAndParentIsNullAndDeletedAtIsNull(String name);

    boolean existsByNameIgnoreCaseAndParentIsNullAndDeletedAtIsNullAndIdNot(String name, Long id);

    /**
     * Kiểm tra trùng tên cho category con trong cùng một parent (children of same root/parent).
     */
    boolean existsByNameIgnoreCaseAndParent_IdAndDeletedAtIsNull(String name, Long parentId);

    boolean existsByNameIgnoreCaseAndParent_IdAndDeletedAtIsNullAndIdNot(String name, Long parentId, Long id);

    @Query("SELECT c FROM category c WHERE c.deletedAt IS NULL AND NOT EXISTS (SELECT ch FROM category ch WHERE ch.parent = c AND ch.deletedAt IS NULL)")
    List<CategoryEntity> findAllLeafCategories();

    // Query đệ quy để lấy chính nó và tất cả các con cháu bên dưới
    @Query(value = "WITH RECURSIVE cat_tree AS (" +
            "SELECT * FROM category WHERE id = :id " +
            "UNION ALL " +
            "SELECT c.* FROM category c INNER JOIN cat_tree ct ON c.parent_id = ct.id) " +
            "SELECT * FROM cat_tree", nativeQuery = true)
    List<CategoryEntity> findAllDescendants(@Param("id") Long id);

    /**
     * Lấy toàn bộ nhánh của một category, bao gồm:
     * - Toàn bộ cha (ancestors) phía trên
     * - Chính nó
     * - Toàn bộ con cháu (descendants) phía dưới
     */
    @Query(value =
            "WITH RECURSIVE cat_anc AS (" +
            "   SELECT * FROM category WHERE id = :id " +
            "   UNION ALL " +
            "   SELECT c.* FROM category c INNER JOIN cat_anc ca ON c.id = ca.parent_id" +
            "), " +
            "cat_desc AS (" +
            "   SELECT * FROM category WHERE id = :id " +
            "   UNION ALL " +
            "   SELECT c.* FROM category c INNER JOIN cat_desc cd ON c.parent_id = cd.id" +
            ") " +
            "SELECT * FROM cat_anc " +
            "UNION " +
            "SELECT * FROM cat_desc",
            nativeQuery = true)
    List<CategoryEntity> findBranchWithAncestors(@Param("id") Long id);
}
