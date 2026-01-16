package com.chobbi.server.catalog.entity;

import com.chobbi.server.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity(name = "attribute_values")
public class AttributeValuesEntity extends BaseEntity {
    @Column(name = "is_custom")
    private Boolean isCustom;
    @Column(name = "value_text")
    private String valueText;
    @Column(name = "value_number")
    private Double valueNumber;
    @Column(name = "value_boolean")
    private Boolean valueBoolean;
    @Column(name = "value_date")
    private LocalDate valueDate;
    @ManyToOne
    @JoinColumn(name = "attribute_id")
    private AttributesEntity attributesEntity;
    @OneToMany(mappedBy = "attributeValuesEntity")
    private List<ProductAttributesEntity> productAttributes = new ArrayList<>();
}
