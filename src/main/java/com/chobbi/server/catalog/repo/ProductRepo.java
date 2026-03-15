package com.chobbi.server.catalog.repo;

import com.chobbi.server.catalog.entity.ProductEntity;
import com.chobbi.server.catalog.enums.StatusEnums;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepo extends JpaRepository<ProductEntity, Long> {
    Optional<ProductEntity> findByIdAndShopEntity_IdAndDeletedAtIsNull(Long productId, Long shopId);
    List<ProductEntity> findByIdInAndShopEntity_IdAndDeletedAtIsNull(Collection<Long> productIds, Long shopId);
    List<ProductEntity> findAllByShopEntity_IdAndDeletedAtIsNull(Long shopId);

    long countByShopEntity_IdAndDeletedAtIsNull(Long shopId);
    long countByShopEntity_IdAndDeletedAtIsNullAndStatus(Long shopId, StatusEnums status);

    /** Sản phẩm thuộc shop, kèm variations + shopCategory (để tính min/max price và shop_category_id). */
    @EntityGraph(attributePaths = {"variations", "shopCategory"})
    @Query("SELECT p FROM product p WHERE p.shopEntity.id = :shopId AND p.deletedAt IS NULL")
    List<ProductEntity> findByShopEntity_IdAndDeletedAtIsNullWithVariations(@Param("shopId") Long shopId);
    Optional<ProductEntity> findByIdAndDeletedAtIsNull(Long productId);
    Optional<ProductEntity> findByVariations_IdAndDeletedAtIsNull(Long variationId);

    Optional<ProductEntity> findByNameAndShopEntity_IdAndDeletedAtIsNull(String name, Long shopId);

    /** Tìm sản phẩm theo tên (chứa keyword), chưa xóa, status ACTIVE. */
    List<ProductEntity> findByNameContainingIgnoreCaseAndDeletedAtIsNullAndStatus(
            String name,
            StatusEnums status
    );

    /** Sản phẩm thuộc category (leaf), chưa xóa, status ACTIVE. */
    List<ProductEntity> findByCategoryEntity_IdAndDeletedAtIsNullAndStatus(
            Long categoryId,
            StatusEnums status
    );

    /** Sản phẩm thuộc 1 trong các category, chưa xóa, status ACTIVE. */
    List<ProductEntity> findByCategoryEntity_IdInAndDeletedAtIsNullAndStatus(
            Collection<Long> categoryIds,
            StatusEnums status
    );

    /** Sản phẩm thuộc 1 danh mục shop cụ thể, chưa xóa. */
    List<ProductEntity> findByShopCategory_IdAndDeletedAtIsNull(Long shopCategoryId);

    /** Sản phẩm thuộc danh mục shop, kèm variations (để tính giá + tồn kho). */
    @EntityGraph(attributePaths = {"variations"})
    @Query("SELECT p FROM product p WHERE p.shopCategory.id = :shopCategoryId AND p.deletedAt IS NULL")
    List<ProductEntity> findByShopCategory_IdAndDeletedAtIsNullWithVariations(@Param("shopCategoryId") Long shopCategoryId);
}
