package com.chobbi.server.catalog.repo;

import com.chobbi.server.catalog.entity.AttributesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface AttributesRepo extends JpaRepository<AttributesEntity, Long> {
    List<AttributesEntity> findByCategoryEntity_IdInAndNameIgnoreCaseAndDeletedAtIsNull(
            Collection<Long> categoryIds,
            String name
    );
}
