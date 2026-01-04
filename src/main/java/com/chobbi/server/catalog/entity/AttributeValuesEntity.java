package com.chobbi.server.catalog.entity;

import com.chobbi.server.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity(name = "attribute_values")
public class AttributeValuesEntity extends BaseEntity {
    @Column(name = "value")
    private String value;
    @Column(name = "is_custom")
    private Boolean isCustom;

    @ManyToOne
    @JoinColumn(name = "attribute_id")
    private AttributesEntity attribute;

    @OneToMany(mappedBy = "attributeValue")
    private List<ProductAttributesEntity> productAttributes = new ArrayList<>();
}
