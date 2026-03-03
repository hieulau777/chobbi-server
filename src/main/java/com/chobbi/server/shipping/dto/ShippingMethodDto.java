package com.chobbi.server.shipping.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShippingMethodDto {
    private Long id;
    private String name;
    /** Cân nặng cơ bản (gram) được tính phí baseFee. */
    private Long baseWeight;
    /** Phí (VND) cho baseWeight gram đầu. */
    private Long baseFee;
    /** Mỗi bước weight_step gram tiếp theo. */
    private Long weightStep;
    /** Phí thêm (VND) cho mỗi weight_step gram. */
    private Long extraFeePerStep;
}
