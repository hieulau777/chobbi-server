package com.chobbi.server.catalog.dto.admin;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AdminCategoryTreeSeedRequest {

    private String name;

    /**
     * Danh sách category con (cây lồng nhau).
     */
    private List<AdminCategoryTreeSeedRequest> children;
}

