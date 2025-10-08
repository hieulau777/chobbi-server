package com.chobbi.server.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddToCartRequest {
    private Long accountId;
    private Long productId;
    private Long variationId;
    private Integer quantity;
}
