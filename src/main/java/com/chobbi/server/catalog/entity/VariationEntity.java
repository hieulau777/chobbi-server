package com.chobbi.server.catalog.entity;

import com.chobbi.server.common.BaseEntity;
import com.chobbi.server.enums.StatusEnums;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity(name ="variation")
public class VariationEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // private String sku;
    private BigDecimal price;

    @Column(name = "price_discount")
    private BigDecimal priceDiscount;
    private Integer stock;
    //private Long weight;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private ProductEntity productEntity;

    @OneToMany(mappedBy = "variationEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VariationOptionEntity> variationOptions = new ArrayList<>();

//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false, length = 20)
//    private StatusEnums status = StatusEnums.ACTIVE;
}
