package com.chobbi.server.repo;

import com.chobbi.server.entity.ShippingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShippingRepo extends JpaRepository<ShippingEntity, Long> {
}
