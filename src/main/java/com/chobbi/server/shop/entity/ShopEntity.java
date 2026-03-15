package com.chobbi.server.shop.entity;

import com.chobbi.server.account.entity.AccountEntity;
import com.chobbi.server.catalog.entity.ProductEntity;
import com.chobbi.server.common.BaseEntity;
import com.chobbi.server.catalog.enums.StatusEnums;
import com.chobbi.server.order.entity.OrderEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity(name = "shop")
public class ShopEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "avatar")
    private String avatar;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private AccountEntity accountEntity;

    @OneToMany(mappedBy = "shopEntity")
    private List<ProductEntity> products = new ArrayList<>();

    @OneToMany(mappedBy = "shopEntity")
    private List<ShopCategoryEntity> shopCategories = new ArrayList<>();

    @OneToMany(mappedBy = "shopEntity")
    private List<ShopBannerEntity> banners = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusEnums status = StatusEnums.ACTIVE;

    @OneToMany(mappedBy = "shopEntity")
    private List<OrderEntity> orders = new ArrayList<>();
}
