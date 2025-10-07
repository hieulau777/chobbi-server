package com.chobbi.server.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity(name = "cart_variation")
public class CartVariation extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    private CartEntity cartEntity;

    @ManyToOne
    @JoinColumn(name = "variation_id")
    private VariationEntity variationEntity;
}
