package com.chobbi.server.shop.repo;

import com.chobbi.server.shop.entity.ShopEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShopRepo extends JpaRepository<ShopEntity, Long> {
    Optional<ShopEntity> findByIdAndDeletedAtIsNull(Long id);
    Optional<ShopEntity> findByProducts_IdAndDeletedAtIsNull(Long id);

    boolean existsByAccountEntity_Id(Long accountId);

    Optional<ShopEntity> findByAccountEntity_IdAndDeletedAtIsNull(Long accountId);
}
