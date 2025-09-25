package com.chobbi.server.repo;

import com.chobbi.server.entity.VariationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VariationRepo extends JpaRepository<VariationEntity, Long> {
    List<VariationEntity> findAllByProductEntity_Id(Long productId);
}
