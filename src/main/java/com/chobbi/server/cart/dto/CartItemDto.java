package com.chobbi.server.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDto {
    private Long cartVariationId;
    private Long variationId;
    private Long productId;
    private String productName;
    private String imageUrl;
    private Integer quantity;
    private BigDecimal price;
    /** Trọng lượng sản phẩm (gram) - dùng để tính phí vận chuyển theo lựa chọn user. */
    private Long weight;
    /** Phân loại: ví dụ [ { "tierName": "Size", "optionName": "S" }, { "tierName": "Màu", "optionName": "Đỏ" } ] */
    private List<VariationOptionDisplayDto> variationOptions = new ArrayList<>();
    /** Nếu true: sản phẩm/biến thể đã bị xóa hoặc ngừng bán, chỉ hiển thị nhưng không cho chọn/checkout. */
    private boolean disabled;
}
