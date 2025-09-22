package com.chobbi.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

//      "id": 1001,
//              "sku": "TS-RED-M",
//              "option_value_ids": [
//              101,
//              201
//              ],
//              "price": 150000,
//              "stock": 10
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductVariantDto {
    private Long id;
    private String sku;
    private List<Long> option_value_ids;
    private BigDecimal price;
    private Long stock;
}
