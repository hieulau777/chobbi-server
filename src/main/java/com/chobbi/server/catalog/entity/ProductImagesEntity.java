package com.chobbi.server.catalog.entity;

import com.chobbi.server.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "product_images")
public class ProductImagesEntity extends BaseEntity {
    private String path;
    @Column(name = "sort_order")
    private Integer sortOrder;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private ProductEntity productEntity;
}
