package com.chobbi.server.order.entity;

import com.chobbi.server.catalog.entity.VariationEntity;
import com.chobbi.server.common.BaseEntity;
import com.fasterxml.jackson.databind.ser.Serializers;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity(name = "order_variation")
public class OrderVariationEntity extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "order_id")
    private OrderEntity orderEntity;

    @ManyToOne
    @JoinColumn(name = "variation_id")
    private VariationEntity variationEntity;

    private Integer quantity;
    private BigDecimal price;

}
