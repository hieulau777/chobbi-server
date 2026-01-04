package com.chobbi.server.catalog.entity;

import com.chobbi.server.common.BaseEntity;
import jakarta.persistence.*;
        import lombok.Data;

@Data
@Entity(name = "variation_option")
public class VariationOptionEntity extends BaseEntity {
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
