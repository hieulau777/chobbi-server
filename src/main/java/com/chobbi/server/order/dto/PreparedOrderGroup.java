package com.chobbi.server.order.dto;
import com.chobbi.server.account.entity.AccountEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PreparedOrderGroup {
    private AccountEntity accountEntity;
    private List<PreparedOrderShop> orderShops = new ArrayList<>();
    private BigDecimal subTotal = BigDecimal.ZERO;
    private BigDecimal totalPrice = BigDecimal.ZERO;
}