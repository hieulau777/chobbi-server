package com.chobbi.server.catalog.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductAttributeDto {
    @NotNull
    private Long id;
    private List<Long> valueIds = new ArrayList<>();
    private List<@NotBlank String> customValues = new ArrayList<>();
}
