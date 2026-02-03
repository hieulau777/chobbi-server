package com.chobbi.server.catalog.repo;

import com.chobbi.server.catalog.entity.TierEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TierRepo extends JpaRepository<TierEntity, Long> {
    List<TierEntity> findAllByIdInAndDeletedAtIsNull(List<Long> tierIds);
    List<TierEntity> findAllByProductEntity_IdAndDeletedAtIsNull(Long productId);
    Optional<TierEntity> findByIdAndProductEntity_IdAndDeletedAtIsNull(Long tierId, Long productId);
}
