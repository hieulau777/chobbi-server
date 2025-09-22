package com.chobbi.server.repo;

import com.chobbi.server.entity.ProductEntity;
import com.chobbi.server.entity.ProductVariantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductVariantRepo extends JpaRepository<ProductVariantEntity, Long> {
    List<ProductVariantEntity> findAllByProductEntity_Id(Long productId);
}
