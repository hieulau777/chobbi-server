package com.chobbi.server.shop.entity;

import com.chobbi.server.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "shop_banner")
public class ShopBannerEntity extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "shop_id", nullable = false)
    private ShopEntity shopEntity;

    @Column(name = "image_path", nullable = false)
    private String imagePath;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}

