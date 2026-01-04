package com.chobbi.server.entity;

import com.chobbi.server.catalog.entity.VariationEntity;
import com.chobbi.server.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity(name = "cart_variation")
public class CartVariationEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer quantity;
    @Column(name = "price_at_time")
    private BigDecimal priceAtTime;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    private CartEntity cartEntity;

    @ManyToOne
    @JoinColumn(name = "variation_id")
    private VariationEntity variationEntity;
}
