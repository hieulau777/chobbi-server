package com.chobbi.server.shop.repo;

import com.chobbi.server.shop.entity.ShopCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopCategoryRepo extends JpaRepository<ShopCategoryEntity, Long> {
    java.util.List<ShopCategoryEntity> findByShopEntity_IdAndDeletedAtIsNullOrderBySortOrderAscIdAsc(Long shopId);

    java.util.Optional<ShopCategoryEntity> findByIdAndShopEntity_IdAndDeletedAtIsNull(Long id, Long shopId);
}

