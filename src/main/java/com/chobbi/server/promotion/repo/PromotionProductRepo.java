package com.chobbi.server.promotion.repo;

import com.chobbi.server.promotion.entity.PromotionProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PromotionProductRepo extends JpaRepository<PromotionProductEntity, Long> {

    List<PromotionProductEntity> findByProductEntity_IdAndPromotionEntity_DeletedAtIsNull(Long productId);

    List<PromotionProductEntity> findByProductEntity_IdAndPromotionEntity_DeletedAtIsNullAndPromotionEntity_StartAtLessThanEqualAndPromotionEntity_EndAtGreaterThanEqual(
            Long productId,
            LocalDateTime startAt,
            LocalDateTime endAt
    );

    void deleteByPromotionEntity_Id(Long promotionId);
}

