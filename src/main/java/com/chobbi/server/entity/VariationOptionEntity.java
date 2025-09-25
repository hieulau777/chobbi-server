package com.chobbi.server.entity;

import jakarta.persistence.*;
        import lombok.Data;

@Data
@Entity(name = "variation_option")
public class VariationOptionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "variation_id")
    private VariationEntity variationEntity;

    @ManyToOne
    @JoinColumn(name = "option_id")
    private OptionsEntity optionsEntity;
}
