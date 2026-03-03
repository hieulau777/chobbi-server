package com.chobbi.server.catalog.repo;

import com.chobbi.server.catalog.entity.VariationOptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VariationOptionRepo extends JpaRepository<VariationOptionEntity, Long> {
    List<VariationOptionEntity> findByVariationEntity_Id(Long productId);
    List<VariationOptionEntity> findAllByVariationEntity_IdIn(List<Long> variantIds);

    @Query("SELECT vo FROM variation_option vo JOIN FETCH vo.variationEntity JOIN FETCH vo.optionsEntity o JOIN FETCH o.tierEntity WHERE vo.variationEntity.id IN :variationIds")
    List<VariationOptionEntity> findByVariationEntity_IdInWithDetails(@Param("variationIds") List<Long> variationIds);
}
