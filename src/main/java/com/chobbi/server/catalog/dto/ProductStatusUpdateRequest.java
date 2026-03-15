package com.chobbi.server.catalog.dto;

import lombok.Data;

@Data
public class ProductStatusUpdateRequest {
    private Long productId;
    /** ACTIVE | DRAFT | INACTIVE */
    private String status;
}

