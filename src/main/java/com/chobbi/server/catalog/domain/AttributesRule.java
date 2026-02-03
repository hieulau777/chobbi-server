package com.chobbi.server.catalog.domain;

import com.chobbi.server.catalog.enums.AttributeTypesEnums;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.beans.BeanProperty;

@Getter
@Setter
public class AttributesRule {
    private boolean required;
    private boolean multipleAllow;
    private boolean customAllow;
    protected AttributeTypesEnums type;
}
