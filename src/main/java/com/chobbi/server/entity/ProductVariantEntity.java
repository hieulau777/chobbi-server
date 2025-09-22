package com.chobbi.server.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Entity(name ="product_variant")
public class ProductVariantEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sku;
    private BigDecimal price;

    @Column(name = "price_discount")
    private BigDecimal priceDiscount;
    private Long stock;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private ProductEntity productEntity;

    @OneToMany(mappedBy = "productVariantEntity")
    private List<ProductVariantOptionEntity> productVariantOptionEntities;
}
