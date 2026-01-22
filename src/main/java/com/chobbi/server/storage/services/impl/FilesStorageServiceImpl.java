package com.chobbi.server.storage.services.impl;

import com.chobbi.server.storage.FolderTypeEnum;
import com.chobbi.server.storage.services.FilesStorageService;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.UUID;

@Service
public class FilesStorageServiceImpl implements FilesStorageService {

    @Value("${file.root-path}")
    private String rootPath;

    @Override
    public String transferStorage(MultipartFile file, FolderTypeEnum folderType) {
        try {
            String folderName = folderType.getFolderName();
            LocalDate now = LocalDate.now();
            String subDir = String.format("%s/%d/%02d/%02d", folderName,
                    now.getYear(), now.getMonthValue(), now.getDayOfMonth());
            Path targetLocation = Paths.get(rootPath).resolve(subDir);
            Files.createDirectories(targetLocation);

            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf(".")) : "";

            String fileName = UUID.randomUUID() + extension;
            Path targetPath = targetLocation.resolve(fileName);

            // Ghi file thô
            file.transferTo(targetPath.toFile());

            return subDir + "/" + fileName;
        } catch (IOException ex) {
            throw new RuntimeException("Lỗi ghi file thô", ex);
        }
    }

    @Override
    public void processOptimization(String relativePath) {
        Path fullPath = Paths.get(rootPath).resolve(relativePath);
        String extension = relativePath.substring(relativePath.lastIndexOf(".") + 1).toLowerCase();

        try {
            BufferedImage image = ImageIO.read(fullPath.toFile());
            if (image == null) return;

            if (image.getWidth() > 1400) {
                image = Scalr.resize(image, Scalr.Method.BALANCED, Scalr.Mode.FIT_TO_WIDTH, 1400);
            }

            saveCompressedImage(image, fullPath, extension);
        } catch (IOException e) {
            System.err.println("Lỗi nén ảnh: " + e.getMessage());
        }
    }

    private void saveCompressedImage(BufferedImage image, Path targetPath, String extension) throws IOException {
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName(extension);
        if (!writers.hasNext()) {
            ImageIO.write(image, extension, targetPath.toFile());
            return;
        }

        ImageWriter writer = writers.next();
        try (ImageOutputStream ios = ImageIO.createImageOutputStream(targetPath.toFile())) {
            writer.setOutput(ios);
            ImageWriteParam param = writer.getDefaultWriteParam();
            if (param.canWriteCompressed() && (extension.equals("jpg") || extension.equals("jpeg"))) {
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                param.setCompressionQuality(0.75f);
            }
            writer.write(null, new IIOImage(image, null, null), param);
        } finally {
            writer.dispose();
        }
    }

    @Override public Resource load(String filename) { return null; }
    @Override public boolean delete(String filename) { return false; }
}