package com.chobbi.server.order.dto;


import com.chobbi.server.shipping.entity.ShippingEntity;
import com.chobbi.server.shop.entity.ShopEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PreparedOrderShop {
    private ShopEntity shopEntity;
    private ShippingEntity shippingEntity;
    private List<PreparedOrderVariations> orderVars = new ArrayList<>();
    private BigDecimal subTotal = BigDecimal.ZERO;
    /** subTotal + shippingCost */
    private BigDecimal totalPrice = BigDecimal.ZERO;
    private BigDecimal shippingCost = BigDecimal.ZERO;
}
