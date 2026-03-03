package com.chobbi.server.shipping.repo;

import com.chobbi.server.shipping.entity.ShippingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ShippingRepo extends JpaRepository<ShippingEntity, Long> {
    Optional<ShippingEntity> findByIdAndDeletedAtIsNull(Long shippingId);

    List<ShippingEntity> findAllByDeletedAtIsNull();
}
