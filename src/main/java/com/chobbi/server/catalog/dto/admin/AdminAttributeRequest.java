package com.chobbi.server.catalog.dto.admin;

import com.chobbi.server.catalog.enums.AttributeTypesEnums;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminAttributeRequest {
    private String name;
    private Boolean isRequired;
    private Boolean isCustomAllow;
    private Boolean isMultipleAllow;
    private AttributeTypesEnums type;
}

