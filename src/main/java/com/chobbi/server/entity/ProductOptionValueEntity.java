package com.chobbi.server.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity(name = "product_option_value")
public class ProductOptionValueEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String value;
    @ManyToOne
    @JoinColumn(name = "product_option_id")
    private ProductOptionEntity productOptionEntity;
}
