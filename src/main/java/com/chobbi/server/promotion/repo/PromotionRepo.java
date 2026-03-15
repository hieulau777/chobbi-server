package com.chobbi.server.promotion.repo;

import com.chobbi.server.promotion.entity.PromotionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PromotionRepo extends JpaRepository<PromotionEntity, Long> {

    List<PromotionEntity> findByShopEntity_IdAndDeletedAtIsNull(Long shopId);

    List<PromotionEntity> findByShopEntity_IdAndDeletedAtIsNullAndStartAtLessThanEqualAndEndAtGreaterThanEqual(
            Long shopId,
            LocalDateTime startAt,
            LocalDateTime endAt
    );

    Optional<PromotionEntity> findByIdAndShopEntity_IdAndDeletedAtIsNull(Long promotionId, Long shopId);
}

