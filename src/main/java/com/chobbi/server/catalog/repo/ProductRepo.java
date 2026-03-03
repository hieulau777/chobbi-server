package com.chobbi.server.catalog.repo;

import com.chobbi.server.catalog.entity.ProductEntity;
import com.chobbi.server.catalog.enums.StatusEnums;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepo extends JpaRepository<ProductEntity, Long> {
    Optional<ProductEntity> findByIdAndShopEntity_IdAndDeletedAtIsNull(Long productId, Long shopId);
    List<ProductEntity> findByIdInAndShopEntity_IdAndDeletedAtIsNull(Collection<Long> productIds, Long shopId);
    List<ProductEntity> findAllByShopEntity_IdAndDeletedAtIsNull(Long shopId);
    Optional<ProductEntity> findByIdAndDeletedAtIsNull(Long productId);
    Optional<ProductEntity> findByVariations_IdAndDeletedAtIsNull(Long variationId);

    Optional<ProductEntity> findByNameAndShopEntity_IdAndDeletedAtIsNull(String name, Long shopId);

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
}
