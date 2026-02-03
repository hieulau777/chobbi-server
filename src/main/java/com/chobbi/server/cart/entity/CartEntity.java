package com.chobbi.server.cart.entity;

import com.chobbi.server.account.entity.AccountEntity;
import com.chobbi.server.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity(name = "cart")
public class CartEntity extends BaseEntity {
    @OneToOne
    @JoinColumn(name = "account_id")
    private AccountEntity accountEntity;

    @OneToMany(mappedBy = "variationEntity", cascade = CascadeType.ALL)
    private List<CartVariationEntity> cartVariations = new ArrayList<>();
}
