package com.chobbi.server.catalog.dto.admin;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class AdminAttributeValueRequest {
    private Boolean isCustom;
    private String valueText;
    private Double valueNumber;
    private Boolean valueBoolean;
    private LocalDate valueDate;
}

