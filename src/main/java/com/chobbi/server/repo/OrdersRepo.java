package com.chobbi.server.repo;

import com.chobbi.server.entity.OrdersEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdersRepo extends JpaRepository<OrdersEntity, Long> {

}
