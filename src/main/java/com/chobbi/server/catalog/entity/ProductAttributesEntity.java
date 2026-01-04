package com.chobbi.server.catalog.entity;

import com.chobbi.server.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity(name = "product_attributes")
public class ProductAttributesEntity extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "product_id")
    private ProductEntity product;
    @ManyToOne
    @JoinColumn(name = "attribute_id")
    private AttributesEntity attribute;

    @ManyToOne
    @JoinColumn(name = "attribute_value_id")
    private AttributeValuesEntity attributeValue;

}
