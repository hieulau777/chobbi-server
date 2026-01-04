package com.chobbi.server.catalog.entity;

import com.chobbi.server.common.BaseEntity;
import com.chobbi.server.entity.ShopEntity;
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
    private String name;
    private String description;
    @ManyToOne
    @JoinColumn(name = "shop_id")
    private ShopEntity shopEntity;
    @OneToMany(mappedBy = "productEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VariationEntity> variations = new ArrayList<>();
    @ManyToOne
    @JoinColumn(name = "category_id")
    private CategoryEntity categoryEntity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusEnums status = StatusEnums.ACTIVE;

    @OneToMany(mappedBy = "productEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TierEntity> tiers = new ArrayList<>();

    @OneToMany(mappedBy = "product")
    private List<ProductAttributesEntity> productAttributes = new ArrayList<>();


}
