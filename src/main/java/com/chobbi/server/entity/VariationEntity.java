package com.chobbi.server.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity(name ="variation")
public class VariationEntity {
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

    @OneToMany(mappedBy = "variationEntity")
    private List<VariationOptionEntity> variationOptionEntityList = new ArrayList<>();
}
