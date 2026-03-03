package com.chobbi.server.shipping.entity;

import com.chobbi.server.common.BaseEntity;
import com.chobbi.server.order.entity.OrderEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "shipping")
public class ShippingEntity extends BaseEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "base_weight")
    private Integer baseWeight;

    @Column(name = "base_fee")
    private BigDecimal baseFee;

    @Column(name = "weight_step")
    private Integer weightStep;

    @Column(name = "extra_fee_per_step")
    private BigDecimal extraFeePerStep;

    @OneToMany(mappedBy = "shippingEntity")
    private List<OrderEntity> orders = new ArrayList<>();
}
