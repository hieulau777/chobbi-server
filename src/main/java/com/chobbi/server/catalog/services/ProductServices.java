package com.chobbi.server.catalog.services;

import com.chobbi.server.catalog.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

public interface ProductServices {
    void createProduct(ProductRequest productRequest, MultipartFile[] media);
    ReadProductDto readProduct(Long productId);
    List<ReadProductSellerDto> listProductsByShopId(Long shopId);
    ReadProductClientDto getProductDetailClient(Long productId);
    /** Danh sách sản phẩm thuộc category lá (cho trang category client). */
    List<ProductCardDto> listProductsByCategoryId(Long categoryId);
    List<ProductCardDto> listProductsByCategoryId(Long categoryId, BigDecimal minPrice, BigDecimal maxPrice);
    List<ProductCardDto> listProductsByCategoryId(Long categoryId, BigDecimal minPrice, BigDecimal maxPrice, List<Long> brandValueIds);
    /** Danh sách brand (attribute value) cho nhánh category (bao gồm leaf con cháu). */
    List<ReadProductAttributeValueDto> listBrandsForCategoryBranch(Long categoryId);
    void updateProduct(ProductRequest productRequest, MultipartFile[] media);
}
