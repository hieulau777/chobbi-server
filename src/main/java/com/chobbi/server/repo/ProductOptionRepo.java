package com.chobbi.server.repo;

import com.chobbi.server.entity.ProductOptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductOptionRepo extends JpaRepository<ProductOptionEntity, Long> {
}
