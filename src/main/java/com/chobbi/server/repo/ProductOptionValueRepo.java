package com.chobbi.server.repo;

import com.chobbi.server.entity.ProductOptionValueEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductOptionValueRepo extends JpaRepository<ProductOptionValueEntity, Long> {
}
