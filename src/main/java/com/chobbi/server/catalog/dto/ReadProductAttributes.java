package com.chobbi.server.catalog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReadProductAttributes {
    private Long id;
    private String name;
    private Boolean isRequired;
    private Boolean isCustomAllow;
    private Boolean isMultipleAllow;
    private String type;
    private List<ReadProductAttributeValueDto> values;
}
