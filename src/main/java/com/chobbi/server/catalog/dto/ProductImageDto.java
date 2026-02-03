package com.chobbi.server.catalog.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductImageDto {
    private Long id; // Có ID = update order; Không ID = thêm mới ảnh [15]
    private String url; // Tên file (dùng khi thêm mới để map với MultipartFile) [16, 17]
    private String name;
    @NotNull
    private Integer sort; // Nếu order = 1, logic sẽ cập nhật thumbnail sản phẩm [15]
}
