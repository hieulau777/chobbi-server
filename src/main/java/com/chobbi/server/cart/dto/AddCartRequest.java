package com.chobbi.server.cart.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AddCartRequest {
    private Long accountId;
    @NotNull
    private Long variationId;
    @NotNull
    private Integer quantity;
}
