package com.chobbi.server.storage.services;

import com.chobbi.server.storage.FolderTypeEnum;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FilesStorageService {

    String transferStorage(MultipartFile file, FolderTypeEnum folderType);

    void processOptimization(String relativePath);

    Resource load(String filename);

    boolean delete(String filename);
}