package com.chobbi.server.entity;

import jakarta.persistence.*;
        import lombok.Data;

@Data
@Entity(name = "product_variant_option")
public class ProductVariantOptionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_variant_id")
    private ProductVariantEntity productVariantEntity;

    @ManyToOne
    @JoinColumn(name = "product_option_value_id")
    private ProductOptionValueEntity productOptionValueEntity;
}
