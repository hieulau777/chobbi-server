package com.chobbi.server.catalog.entity;

import com.chobbi.server.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity(name = "tier")
public class TierEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @OneToMany(mappedBy = "tierEntity", cascade = CascadeType.ALL)
    private List<OptionsEntity> options = new ArrayList<>();
    @ManyToOne
    @JoinColumn(name = "product_id")
    private ProductEntity productEntity;
}
