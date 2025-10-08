package com.chobbi.server.entity;

import com.chobbi.server.enums.StatusEnums;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity(name = "product")
public class ProductEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    @ManyToOne
    @JoinColumn(name = "shop_id")
    private ShopEntity shopEntity;
    @OneToMany(mappedBy = "productEntity")
    private List<VariationEntity> variations = new ArrayList<>();
    @ManyToOne
    @JoinColumn(name = "category_id")
    private CategoryEntity categoryEntity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusEnums status = StatusEnums.ACTIVE;

    @OneToMany(mappedBy = "productEntity")
    private List<TierEntity> tiers = new ArrayList<>();
}
