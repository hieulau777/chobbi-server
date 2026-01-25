package com.chobbi.server.catalog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReadProductTierDto {
    private Long id;
    private String name;
    private List<ReadProductTierOptionDto> options;
    private Boolean hasImages;

}
