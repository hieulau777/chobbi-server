package com.chobbi.server.catalog.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AdminCategoryResponse {
    private Long id;
    private String name;
    private Long parentId;
}

