package com.chobbi.server.guide.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "seller_usage_guides")
public class SellerUsageGuideDocument {

    @Id
    private String id;

    /**
     * Target audience of this guide.
     * For now we only support "SELLER" but keep this extensible.
     */
    private String target;

    private String title;

    private String content;

    private String youtubeUrl;

    /**
     * JSON cấu hình sản phẩm demo để seed cho seller.
     * Format: AdminProductSeedRequest (giống file product_seed_thoitrangnam_aothun.json).
     */
    private String seedConfigJson;

    /**
     * Whether the "seed data" button on seller UI should be visible/enabled.
     */
    private boolean seedButtonEnabled;

    private Instant updatedAt;
}

