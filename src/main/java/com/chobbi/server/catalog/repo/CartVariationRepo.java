package com.chobbi.server.catalog.repo;

import com.chobbi.server.cart.entity.CartEntity;
import com.chobbi.server.cart.entity.CartVariationEntity;
import com.chobbi.server.catalog.entity.VariationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CartVariationRepo extends JpaRepository<CartVariationEntity, Long> {
    Optional<CartVariationEntity> findByCartEntityAndVariationEntity(CartEntity cart, VariationEntity variation);
    List<CartVariationEntity> findAllByCartEntity(CartEntity cart);

    @Query("SELECT cv FROM cart_variation cv " +
           "JOIN FETCH cv.variationEntity v " +
           "JOIN FETCH v.productEntity p " +
           "JOIN FETCH p.shopEntity " +
           "WHERE cv.cartEntity.accountEntity.id = :accountId " +
           "AND cv.deletedAt IS NULL")
    List<CartVariationEntity> findByAccountIdWithDetails(@Param("accountId") Long accountId);
}
