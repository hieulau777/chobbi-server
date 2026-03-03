package com.chobbi.server.cart.entity;

import com.chobbi.server.catalog.entity.VariationEntity;
import com.chobbi.server.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity(name = "cart_variation")
public class CartVariationEntity extends BaseEntity {

    private Integer quantity;

    @Column(name = "price_at_time", nullable = false)
    private BigDecimal priceAtTime;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    private CartEntity cartEntity;

    @ManyToOne
    @JoinColumn(name = "variation_id")
    private VariationEntity variationEntity;
}
