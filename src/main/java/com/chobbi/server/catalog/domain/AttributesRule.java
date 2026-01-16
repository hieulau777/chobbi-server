package com.chobbi.server.catalog.domain;

import com.chobbi.server.catalog.enums.AttributeTypesEnums;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AttributesRule {
    private boolean required;
    private boolean multipleAllow;
    private boolean customAllow;
    private AttributeTypesEnums type;
}
