package com.chobbi.server.catalog.dto;

import lombok.Data;

@Data
public class UpdateProductVariationCombinationDto {
    private Long tierId;   // ID của Tier (mới tạo hoặc đã có)
    private Long optionId; // ID của Option (mới tạo hoặc đã có)
    // Nếu là tier/option mới tạo trong cùng request, FE có thể gửi name để BE map tạm thời
    private String tierName;
    private String optionName;
}
