package com.chobbi.server.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PublicShopCategoryRefDto {
    private Long id;
    private String name;
}
