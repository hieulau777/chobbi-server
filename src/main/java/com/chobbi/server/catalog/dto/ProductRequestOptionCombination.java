package com.chobbi.server.catalog.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequestOptionCombination {
    Long tierId;
    @NotBlank
    private String tierName;
    Long optionId;
    @NotBlank
    private String optionName;
}
