package com.chobbi.server.catalog.dto;

import lombok.Data;

@Data
public class ProductOptionImageDto {
    private Long imageId; // ID của ProductImage hiện có (nếu dùng lại ảnh cũ)
    private Long tierId;   // Để xác định Tier
    private String tierName;
    private Long optionId; // Để xác định Option
    private String optionName;
    private String imageName; // Dùng khi upload ảnh mới cho option (có thể null nếu dùng imageId)
}
