package com.chobbi.server.catalog.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductRequestOptionCombination {
    @NotBlank
    private String tierName;
    @NotBlank
    private String optionName;
}
