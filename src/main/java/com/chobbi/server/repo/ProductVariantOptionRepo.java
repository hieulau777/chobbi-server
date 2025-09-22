package com.chobbi.server.repo;

import com.chobbi.server.entity.ProductVariantOptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductVariantOptionRepo extends JpaRepository<ProductVariantOptionEntity, Long> {
    List<ProductVariantOptionEntity> findByProductVariantEntity_Id(Long productId);
    List<ProductVariantOptionEntity> findAllByProductVariantEntity_IdIn(List<Long> variantIds);

}
