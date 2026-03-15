package com.chobbi.server.catalog.dto.admin;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminCategoryRequest {
    private String name;
    /**
     * null = root
     */
    private Long parentId;
}

