package com.chobbi.server.order.repo;

import com.chobbi.server.order.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepo extends JpaRepository<OrderEntity, Long> {

    @Query("SELECT DISTINCT o FROM orders o " +
            "LEFT JOIN FETCH o.orderGroupEntity " +
            "LEFT JOIN FETCH o.accountEntity " +
            "LEFT JOIN FETCH o.shippingEntity " +
            "LEFT JOIN FETCH o.orderVariations ov " +
            "LEFT JOIN FETCH ov.variationEntity v " +
            "LEFT JOIN FETCH v.productEntity p " +
            "WHERE o.shopEntity.id = :shopId AND o.deletedAt IS NULL AND o.status = :status " +
            "ORDER BY o.createdAt DESC")
    List<OrderEntity> findByShopIdAndStatusWithDetails(@Param("shopId") Long shopId,
                                                       @Param("status") String status);

    @Query("SELECT o FROM orders o " +
            "JOIN FETCH o.shopEntity s " +
            "JOIN FETCH s.accountEntity sa " +
            "WHERE o.id = :orderId AND o.deletedAt IS NULL")
    Optional<OrderEntity> findByIdWithShopOwner(@Param("orderId") Long orderId);
}
