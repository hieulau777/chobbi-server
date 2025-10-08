package com.chobbi.server.repo;

import com.chobbi.server.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepo extends JpaRepository<ProductEntity, Long> {
    Optional<ProductEntity> findByIdAndShopEntity_IdAndDeletedAtIsNull(Long productId, Long shopId);
    List<ProductEntity> findAllByShopEntity_IdAndDeletedAtIsNull(Long shopId);
}
