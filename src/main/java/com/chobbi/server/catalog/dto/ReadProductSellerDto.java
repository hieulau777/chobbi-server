package com.chobbi.server.catalog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReadProductSellerDto {
    private Long id;
    private String img;
    private String name;
    private List<ReadProductVariationSellerDto> variations;

}
