package com.chobbi.server.repo;

import com.chobbi.server.catalog.entity.VariationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VariationRepo extends JpaRepository<VariationEntity, Long> {
    List<VariationEntity> findAllByProductEntity_IdAndDeletedAtIsNull(Long productId);
    List<VariationEntity> findAllByProductEntity_Id(Long productId);
    Optional<VariationEntity> findByIdAndDeletedAtIsNull(Long variationId);
}
