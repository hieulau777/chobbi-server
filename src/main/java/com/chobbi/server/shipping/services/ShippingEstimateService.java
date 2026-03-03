package com.chobbi.server.shipping.services;

import com.chobbi.server.shipping.dto.ShopProductIdsRequest;
import com.chobbi.server.shipping.dto.ShopShippingOptionsDto;
import com.chobbi.server.shipping.dto.ShippingOptionDto;

import java.util.List;

public interface ShippingEstimateService {
    /**
     * Ước tính phí giao hàng theo từng phương thức khi biết tổng trọng lượng (gram).
     * Dùng khi chỉ có weight (vd: client tự tính tổng weight và gọi API).
     */
    List<ShippingOptionDto> estimateByWeight(long weightGram);

    /**
     * Tính phí giao hàng theo từng phương thức cho mỗi shop, dựa trên tổng trọng lượng các sản phẩm.
     */
    List<ShopShippingOptionsDto> estimateByShopProducts(List<ShopProductIdsRequest> request);
}
