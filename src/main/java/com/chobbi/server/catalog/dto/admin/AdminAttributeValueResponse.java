package com.chobbi.server.catalog.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class AdminAttributeValueResponse {
    private Long id;
    private Long attributeId;
    private Boolean isCustom;
    private String valueText;
    private Double valueNumber;
    private Boolean valueBoolean;
    private LocalDate valueDate;
}

