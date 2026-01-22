package com.chobbi.server.catalog.entity;

import com.chobbi.server.common.BaseEntity;
import com.chobbi.server.entity.ShopEntity;
import com.chobbi.server.enums.StatusEnums;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity(name = "product")
public class ProductEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    @NotBlank(message = "Sản phẩm phải có thumbnail")
    private String thumbnail;
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

    @OneToMany(mappedBy = "productEntity", cascade = CascadeType.ALL)
    private List<TierEntity> tiers = new ArrayList<>();

    @OneToMany(mappedBy = "productEntity", cascade = CascadeType.ALL)
    private List<ProductAttributesEntity> productAttributes = new ArrayList<>();

    @OneToMany(mappedBy = "productEntity", cascade = CascadeType.ALL)
    private List<ProductImagesEntity> productImages = new ArrayList<>();
}
