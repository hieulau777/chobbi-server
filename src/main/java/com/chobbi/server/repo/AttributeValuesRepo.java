package com.chobbi.server.repo;

import com.chobbi.server.catalog.entity.AttributeValuesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttributeValuesRepo extends JpaRepository<AttributeValuesEntity, Long> {
}
