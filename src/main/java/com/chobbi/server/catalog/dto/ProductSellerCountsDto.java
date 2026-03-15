package com.chobbi.server.catalog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductSellerCountsDto {
    private long all;
    private long active;
    private long draft;
}
