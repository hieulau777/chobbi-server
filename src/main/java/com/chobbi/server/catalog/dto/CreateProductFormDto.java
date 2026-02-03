package com.chobbi.server.catalog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductFormDto {
    private String name;
    private List<CategoryDto> categoryTree;
    private List<ReadProductAttributes> attributes;

}
