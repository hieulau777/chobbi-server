package com.chobbi.server.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class OrderRequest {

    @NotNull(message = "Shop ID không được để trống")
    private Long shopId;

    @NotNull(message = "Vui lòng chọn phương thức vận chuyển")
    private Long shippingId;

    @NotNull(message = "Vui lòng chọn địa chỉ nhận hàng")
    private Long addressId;

    /** Phí vận chuyển (VND) đã chốt cho shop này. */
    private Long shippingCost = 0L;

    @NotEmpty(message = "Danh sách sản phẩm không được để trống")
    @Valid
    private List<OrderVariationDto> variations = new ArrayList<>();

}
