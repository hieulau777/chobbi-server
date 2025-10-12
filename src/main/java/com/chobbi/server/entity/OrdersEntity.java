package com.chobbi.server.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity(name = "orders")
public class OrdersEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private AccountEntity accountEntity;

    @ManyToOne
    @JoinColumn(name = "shipping_id")
    private ShippingEntity shippingEntity;
}