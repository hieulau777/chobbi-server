package com.chobbi.server.order.entity;

import com.chobbi.server.account.entity.AccountEntity;
import com.chobbi.server.address.entity.AddressEntity;
import com.chobbi.server.common.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity(name = "order_groups")
public class OrderGroupEntity extends BaseEntity {
    private String code;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    @Column(name = "sub_total")
    private BigDecimal subTotal;

    @OneToMany(mappedBy = "orderGroupEntity", cascade = CascadeType.PERSIST)
    private List<OrderEntity> orders = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "account_id")
    private AccountEntity accountEntity;

    @ManyToOne
    @JoinColumn(name = "address_id", nullable = false)
    private AddressEntity addressEntity;
}

