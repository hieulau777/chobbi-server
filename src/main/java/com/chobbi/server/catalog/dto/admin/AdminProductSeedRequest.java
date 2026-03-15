package com.chobbi.server.catalog.dto.admin;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.List;
@Getter
@Setter
public class AdminProductSeedRequest {
    private String email;
    private List<ProductSeedItem> products;
    @Getter
    @Setter
    public static class ProductSeedItem {
        private CategoryPath categoryPath;
        private String name;
        private String description;
        private Long weight;
        private List<SeedAttribute> attributes;              // thuộc tính cấp product (Thương hiệu,…)
        private List<SeedTier> tiers;                        // null/empty = single, có = multi
        private List<SeedVariation> variations;              // danh sách biến thể
    }
    @Getter
    @Setter
    public static class CategoryPath {
        private String rootName;   // "Thời trang nam"
        private String leafName;   // "Áo thun"
    }
    @Getter
    @Setter
    public static class SeedAttribute {
        private String attributeName;  // "Thương hiệu"
        private String valueName;      // "Uniqlo"
    }
    @Getter
    @Setter
    public static class SeedTier {
        private String name;           // "Màu sắc", "Size"
        private Boolean hasImages;     // true cho tier có ảnh (ví dụ Màu sắc), false/null cho tier còn lại
        private List<String> options;  // ["Đen", "Trắng", ...]
    }
    @Getter
    @Setter
    public static class SeedVariation {
        private BigDecimal price;
        private BigDecimal discountPrice;
        private Integer stock;
        private List<SeedOptionRef> options;  // mapping tier + option
    }
    @Getter
    @Setter
    public static class SeedOptionRef {
        private String tierName;      // "Màu sắc"
        private String optionName;    // "Đen"
    }
}
