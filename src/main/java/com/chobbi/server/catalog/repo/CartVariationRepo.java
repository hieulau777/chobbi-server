package com.chobbi.server.catalog.repo;

import com.chobbi.server.cart.entity.CartEntity;
import com.chobbi.server.cart.entity.CartVariationEntity;
import com.chobbi.server.catalog.entity.VariationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartVariationRepo extends JpaRepository<CartVariationEntity, Long> {
    Optional<CartVariationEntity> findByCartEntityAndVariationEntity(CartEntity cart, VariationEntity variation);
    List<CartVariationEntity> findAllByCartEntity(CartEntity cart);
}
