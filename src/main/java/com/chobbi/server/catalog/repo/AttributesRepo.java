package com.chobbi.server.catalog.repo;

import com.chobbi.server.catalog.entity.AttributesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttributesRepo extends JpaRepository<AttributesEntity, Long> {
}
