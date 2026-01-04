package com.chobbi.server.repo;

import com.chobbi.server.entity.ShopEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShopRepo extends JpaRepository<ShopEntity, Long> {
    Optional<ShopEntity> findByIdAndDeletedAtIsNull(Long id);
}
