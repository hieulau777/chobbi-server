package com.chobbi.server.shipping.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShopShippingOptionsDto {
    private Long shopId;
    private List<ShippingOptionDto> options;
}
