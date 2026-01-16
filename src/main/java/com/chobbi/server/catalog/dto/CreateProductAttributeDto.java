package com.chobbi.server.catalog.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductAttributeDto {
    @NotNull
    private Long id;
    private List<Long> valueIds;
    private List<@NotBlank String> customValues;
}
