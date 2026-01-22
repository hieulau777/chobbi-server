package com.chobbi.server.catalog.repo;

import com.chobbi.server.catalog.entity.VariationOptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VariationOptionRepo extends JpaRepository<VariationOptionEntity, Long> {
    List<VariationOptionEntity> findByVariationEntity_Id(Long productId);
    List<VariationOptionEntity> findAllByVariationEntity_IdIn(List<Long> variantIds);

}
