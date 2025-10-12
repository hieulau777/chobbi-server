package com.chobbi.server.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity(name = "shipping")
public class ShippingEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @OneToMany(mappedBy = "shippingEntity")
    private List<OrdersEntity> ordersEntities = new ArrayList<>();
}
