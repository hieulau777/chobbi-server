package com.chobbi.server.catalog.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProductRequest {
    @NotNull
    private Long shopId;
    @NotNull
    private Long productId;
    private Long categoryId;
    

}
