package com.chobbi.server.catalog.entity;

import com.chobbi.server.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity(name = "options")
public class OptionsEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(name = "img_path")
    private String imgPath;
    @ManyToOne
    @JoinColumn(name = "tier_id")
    private TierEntity tierEntity;

    @OneToMany(mappedBy = "optionsEntity")
    private List<VariationOptionEntity> variationOptions = new ArrayList<>();
}
