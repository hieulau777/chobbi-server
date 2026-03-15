package com.chobbi.server.guide.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SellerUsageGuideDto {

    private String id;

    private String title;

    private String content;

    private String youtubeUrl;

    private boolean seedButtonEnabled;

    /**
     * JSON cấu hình sản phẩm demo để seed cho seller.
     * Admin paste JSON này trong chobbi-admin.
     */
    private String seedConfigJson;
}

