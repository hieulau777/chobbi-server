package com.chobbi.server.cart.dto;

import com.chobbi.server.shipping.dto.ShippingOptionDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartShopGroupDto {
    private Long shopId;
    private String shopName;
    private List<CartItemDto> items = new ArrayList<>();
    /** Phương thức giao hàng và chi phí ước tính theo tổng trọng lượng sản phẩm của shop. */
    private List<ShippingOptionDto> shippingOptions = new ArrayList<>();
}
