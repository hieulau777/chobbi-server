package com.chobbi.server.entity;

import com.chobbi.server.catalog.entity.ProductEntity;
import com.chobbi.server.common.BaseEntity;
import com.chobbi.server.enums.StatusEnums;
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
    @OneToMany(mappedBy = "shopEntity")
    private List<ProductEntity> products = new ArrayList<>();
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusEnums status = StatusEnums.ACTIVE;
}
