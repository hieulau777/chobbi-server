package com.chobbi.server.shipping.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShippingOptionDto {
    /** Id phương thức giao hàng (bảng shipping). */
    private Long id;
    /** Tên phương thức giao hàng. */
    private String methodName;
    /** Chi phí (VND) ước tính - có thể tính lại phía client theo tổng trọng lượng được chọn. */
    private Long cost;
    /** Công thức: gram đầu baseWeight = baseFee; mỗi weightStep gram tiếp = extraFeePerStep. */
    private Long baseWeight;
    private Long baseFee;
    private Long weightStep;
    private Long extraFeePerStep;
}
