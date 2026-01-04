package com.chobbi.server.repo;

import com.chobbi.server.catalog.entity.TierEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TierRepo extends JpaRepository<TierEntity, Long> {
    List<TierEntity> findAllByIdInAndDeletedAtIsNull(List<Long> tierIds);
    List<TierEntity> findAllByProductEntity_IdAndDeletedAtIsNull(Long productId);
}
