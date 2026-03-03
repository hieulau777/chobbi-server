package com.chobbi.server.order.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderVariationDto {
    @NotNull(message = "Variation ID không được để trống")
    private Long variationId;

    @NotNull(message = "Số lượng không được để trống")
    private Integer quantity;
}
