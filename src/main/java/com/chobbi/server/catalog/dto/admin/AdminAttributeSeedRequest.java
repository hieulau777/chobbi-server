package com.chobbi.server.catalog.dto.admin;

import com.chobbi.server.catalog.enums.AttributeTypesEnums;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AdminAttributeSeedRequest {

    /**
     * Category đích để seed attributes theo ID.
     * Service sẽ validate đây là leaf-category. Ưu tiên categoryId nếu cả hai cùng có.
     */
    private Long categoryId;

    /**
     * Hoặc truyền theo đường dẫn category: root + leaf.
     * Ví dụ: { "rootName": "Thời trang nam", "leafName": "Áo thun" }.
     */
    private CategoryPath categoryPath;

    /**
     * Danh sách attributes cần seed vào category.
     */
    private List<AttributeWithValues> attributes;

    @Getter
    @Setter
    public static class AttributeWithValues {
        private String name;
        private Boolean isRequired;
        private Boolean isCustomAllow;
        private Boolean isMultipleAllow;
        private AttributeTypesEnums type;

        /**
         * Optional: danh sách attribute values để seed cho attribute này.
         */
        private List<AttributeValueSeed> values;
    }

    @Getter
    @Setter
    public static class AttributeValueSeed {
        private Boolean isCustom;
        private String valueText;
        private Double valueNumber;
        private Boolean valueBoolean;
        private String valueDate; // ISO date string, sẽ parse sang LocalDate nếu cần
    }

    @Getter
    @Setter
    public static class CategoryPath {
        private String rootName;
        private String leafName;
    }
}

