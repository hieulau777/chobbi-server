package com.chobbi.server.order.repo;

import com.chobbi.server.order.entity.OrderGroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderGroupRepo extends JpaRepository<OrderGroupEntity, Long> {
    @Query("SELECT DISTINCT og FROM order_groups og " +
            "LEFT JOIN FETCH og.orders o " +
            "LEFT JOIN FETCH o.shopEntity s " +
            "LEFT JOIN FETCH o.shippingEntity sh " +
            "LEFT JOIN FETCH o.orderVariations ov " +
            "LEFT JOIN FETCH ov.variationEntity v " +
            "LEFT JOIN FETCH v.productEntity p " +
            "WHERE og.accountEntity.id = :accountId AND og.deletedAt IS NULL " +
            "ORDER BY og.createdAt DESC")
    List<OrderGroupEntity> findByAccountIdWithDetails(@Param("accountId") Long accountId);
}
