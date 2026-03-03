package com.chobbi.server.account.entity;

import com.chobbi.server.address.entity.AddressEntity;
import com.chobbi.server.common.BaseEntity;
import com.chobbi.server.order.entity.OrderEntity;
import com.chobbi.server.order.entity.OrderGroupEntity;
import com.chobbi.server.shop.entity.ShopEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity(name = "account")
public class AccountEntity extends BaseEntity {
    private String email;
    private String name;

    @Column(nullable = true)
    private String pwd;

    @Column(name = "phone")
    private String phone;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @OneToMany(mappedBy = "accountEntity")
    private List<AccountRolesEntity> accountRoles = new ArrayList<>();

    @OneToMany(mappedBy = "accountEntity")
    private List<OrderEntity> orders = new ArrayList<>();

    @OneToMany(mappedBy = "accountEntity")
    private List<OrderGroupEntity> orderGroups = new ArrayList<>();

    @OneToMany(mappedBy = "accountEntity")
    private List<SocialAccountEntity> socialAccounts = new ArrayList<>();

    @OneToMany(mappedBy = "accountEntity")
    private List<ShopEntity> shops = new ArrayList<>();

    @OneToMany(mappedBy = "accountEntity")
    private List<AddressEntity> addresses = new ArrayList<>();
}

