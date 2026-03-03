package com.chobbi.server.shipping.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShopProductIdsRequest {
    @NotNull(message = "Shop id là bắt buộc")
    private Long shopId;

    @NotNull(message = "Danh sách sản phẩm không được null")
    private List<Long> productIds;
}
