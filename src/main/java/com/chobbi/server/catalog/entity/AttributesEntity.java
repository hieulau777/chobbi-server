package com.chobbi.server.catalog.entity;

import com.chobbi.server.catalog.enums.AttributeTypesEnums;
import com.chobbi.server.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity(name = "attributes")
public class AttributesEntity extends BaseEntity {
    private String name;
    @Column(name = "is_required")
    private Boolean isRequired;
    @Column(name = "is_custom_allow")
    private Boolean isCustomAllow;
    @Column(name = "is_multiple_allow")
    private Boolean isMultipleAllow;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttributeTypesEnums type;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private CategoryEntity categoryEntity;

    @OneToMany(mappedBy = "attributesEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AttributeValuesEntity> attributeValues = new ArrayList<>();

    @OneToMany(mappedBy = "attributesEntity")
    private List<ProductAttributesEntity> productAttributes = new ArrayList<>();
}
