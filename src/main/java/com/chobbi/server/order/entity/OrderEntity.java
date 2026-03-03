package com.chobbi.server.order.entity;

import com.chobbi.server.account.entity.AccountEntity;
import com.chobbi.server.common.BaseEntity;
import com.chobbi.server.shop.entity.ShopEntity;
import com.chobbi.server.shipping.entity.ShippingEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity(name ="orders")
public class OrderEntity extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "account_id")
    private AccountEntity accountEntity;

    @ManyToOne
    @JoinColumn(name = "shop_id")
    private ShopEntity shopEntity;

    @ManyToOne
    @JoinColumn(name = "shipping_id")
    private ShippingEntity shippingEntity;

    @Column(name = "total_price")
    private BigDecimal totalPrice;

    @Column(name = "shipping_cost")
    private BigDecimal shippingCost;

    @Column(name = "status", insertable = false)
    private String status;

    @ManyToOne
    @JoinColumn(name = "order_group_id")
    private OrderGroupEntity orderGroupEntity;

    @OneToMany(mappedBy = "orderEntity", cascade = jakarta.persistence.CascadeType.PERSIST)
    private List<OrderVariationEntity> orderVariations = new ArrayList<>();
}
