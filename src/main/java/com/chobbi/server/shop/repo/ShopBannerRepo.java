package com.chobbi.server.shop.repo;

import com.chobbi.server.shop.entity.ShopBannerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShopBannerRepo extends JpaRepository<ShopBannerEntity, Long> {

    List<ShopBannerEntity> findByShopEntity_IdAndDeletedAtIsNullOrderBySortOrderAscIdAsc(Long shopId);

    Optional<ShopBannerEntity> findByIdAndShopEntity_IdAndDeletedAtIsNull(Long id, Long shopId);

    long countByShopEntity_IdAndDeletedAtIsNull(Long shopId);
}
