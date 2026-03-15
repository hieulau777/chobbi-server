package com.chobbi.server.promotion.entity;

import com.chobbi.server.catalog.entity.ProductEntity;
import com.chobbi.server.common.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "promotion_product")
public class PromotionProductEntity extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "promotion_id", nullable = false)
    private PromotionEntity promotionEntity;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity productEntity;
}

