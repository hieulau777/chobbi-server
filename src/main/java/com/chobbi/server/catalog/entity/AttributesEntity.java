package com.chobbi.server.catalog.entity;

import com.chobbi.server.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity(name = "attributes")
public class AttributesEntity extends BaseEntity {
    @Column(name = "name")
    private String name;
    @Column(name = "is_required")
    private Boolean isRequired;
    @Column(name = "is_custom_allow")
    private Boolean isCustomAllow;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private CategoryEntity category;

    @OneToMany(mappedBy = "attribute")
    private List<AttributeValuesEntity> attributeValues =new ArrayList<>();

    @OneToMany(mappedBy = "attribute")
    private List<ProductAttributesEntity> productAttributes = new ArrayList<>();

}
