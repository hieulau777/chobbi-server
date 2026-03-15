package com.chobbi.server.promotion.entity;

import com.chobbi.server.common.BaseEntity;
import com.chobbi.server.shop.entity.ShopEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity(name = "promotion")
public class PromotionEntity extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "shop_id", nullable = false)
    private ShopEntity shopEntity;

    @Column(nullable = false)
    private String name;

    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;

    @Column(name = "end_at", nullable = false)
    private LocalDateTime endAt;

    @OneToMany(mappedBy = "promotionEntity")
    private List<PromotionProductEntity> promotionProducts = new ArrayList<>();
}

