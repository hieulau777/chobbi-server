package com.chobbi.server.catalog.dto.admin;

import com.chobbi.server.catalog.enums.AttributeTypesEnums;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AdminAttributeResponse {
    private Long id;
    private Long categoryId;
    private String name;
    private Boolean isRequired;
    private Boolean isCustomAllow;
    private Boolean isMultipleAllow;
    private AttributeTypesEnums type;
}

