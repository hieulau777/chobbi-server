package com.chobbi.server.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity(name = "orders")
public class OrderVariationEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "order_id")
    private OrdersEntity ordersEntity;
    @ManyToOne
    @JoinColumn(name = "variation_id")
    private VariationEntity variationEntity;
}
