package com.chobbi.server.dto.cart;

import com.chobbi.server.dto.TierDto;
import com.chobbi.server.dto.VariationDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartShopDto {
    private Long shopId;
    private String shopName;
    private List<CartProductDto> product;
}
