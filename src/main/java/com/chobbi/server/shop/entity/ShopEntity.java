package com.chobbi.server.shop.entity;

import com.chobbi.server.account.entity.AccountEntity;
import com.chobbi.server.catalog.entity.ProductEntity;
import com.chobbi.server.common.BaseEntity;
import com.chobbi.server.catalog.enums.StatusEnums;
import com.chobbi.server.order.entity.OrderEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity(name = "shop")
public class ShopEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private AccountEntity accountEntity;
    @OneToMany(mappedBy = "shopEntity")
    private List<ProductEntity> products = new ArrayList<>();
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusEnums status = StatusEnums.ACTIVE;

    @OneToMany(mappedBy = "shopEntity")
    private List<OrderEntity> orders = new ArrayList<>();
}
