package com.chobbi.server.catalog.entity;

import com.chobbi.server.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity(name = "category")
public class CategoryEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Long parentId;
    @OneToMany(mappedBy = "categoryEntity")
    private List<ProductEntity> productEntities = new ArrayList<>();
    @OneToMany(mappedBy = "categoryEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AttributesEntity> attributes = new ArrayList<>();
}
