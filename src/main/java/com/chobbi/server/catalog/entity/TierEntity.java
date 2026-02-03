package com.chobbi.server.catalog.entity;

import com.chobbi.server.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity(name = "tier")
public class TierEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(name = "has_images")
    private Boolean hasImages;
    @OneToMany(mappedBy = "tierEntity", cascade = CascadeType.ALL)
    private List<OptionsEntity> options = new ArrayList<>();
    @ManyToOne
    @JoinColumn(name = "product_id")
    private ProductEntity productEntity;
}
