package com.chobbi.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderProductRequestDto {
    private Long productId;
    private Long variationId;
    private Integer quantity;
    private Long shopId;
    private Long shippingId;
}
