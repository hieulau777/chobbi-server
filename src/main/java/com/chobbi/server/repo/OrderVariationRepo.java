package com.chobbi.server.repo;

import com.chobbi.server.entity.OrderVariationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderVariationRepo extends JpaRepository<OrderVariationEntity, Long> {
}
