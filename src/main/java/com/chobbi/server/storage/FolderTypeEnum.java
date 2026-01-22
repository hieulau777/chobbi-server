package com.chobbi.server.storage;

import lombok.Getter;

@Getter
public enum FolderTypeEnum {
    PRODUCTS("products"),
    AVATARS("avatars"),
    REVIEWS("reviews");

    private final String folderName;

    FolderTypeEnum(String folderName) {
        this.folderName = folderName;
    }
}
