package com.chobbi.server.shop.dto;

import com.chobbi.server.catalog.enums.StatusEnums;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ShopResponse {
    private Long id;
    private String name;
    private String avatar;
    private StatusEnums status;
}

