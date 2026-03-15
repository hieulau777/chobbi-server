package com.chobbi.server.shop.entity;

import com.chobbi.server.catalog.entity.ProductEntity;
import com.chobbi.server.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity(name = "shop_category")
public class ShopCategoryEntity extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "shop_id", nullable = false)
    private ShopEntity shopEntity;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @OneToMany(mappedBy = "shopCategory")
    private List<ProductEntity> products = new ArrayList<>();
}

