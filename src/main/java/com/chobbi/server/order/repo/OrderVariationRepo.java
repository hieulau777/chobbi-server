package com.chobbi.server.order.repo;

import com.chobbi.server.order.entity.OrderVariationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderVariationRepo extends JpaRepository<OrderVariationEntity, Long> {
}
