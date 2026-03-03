package com.chobbi.server.order.repo;

import com.chobbi.server.order.entity.OrderGroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderGroupRepo extends JpaRepository<OrderGroupEntity, Long> {
}
