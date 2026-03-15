package com.chobbi.server.storage.services;

import com.chobbi.server.storage.FolderTypeEnum;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FilesStorageService {

    String transferStorage(MultipartFile file, FolderTypeEnum folderType);

    /** Lưu file vào thư mục tương đối (vd: shop/1/banner). Trả về đường dẫn tương đối. */
    String transferToFolder(MultipartFile file, String relativeFolderPath);

    void processOptimization(String relativePath);

    Resource load(String filename);

    boolean delete(String filename);

    /** Xóa file theo đường dẫn tương đối (từ rootPath). */
    boolean deleteByRelativePath(String relativePath);
}