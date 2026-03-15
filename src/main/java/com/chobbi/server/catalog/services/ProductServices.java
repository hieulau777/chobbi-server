package com.chobbi.server.catalog.services;

import com.chobbi.server.catalog.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

public interface ProductServices {
    void createProduct(ProductRequest productRequest, MultipartFile[] media);
    ReadProductDto readProduct(Long productId);

    /** Danh sách sản phẩm của 1 shop (seller). */
    List<ReadProductSellerDto> listProductsByShopId(Long shopId);

    /** Danh sách sản phẩm của 1 shop (seller) + filter tên, giá, category leaf. */
    List<ReadProductSellerDto> listProductsByShopId(Long shopId,
                                                    String nameKeyword,
                                                    BigDecimal minPrice,
                                                    BigDecimal maxPrice,
                                                    Long categoryId);

    /**
     * Phân trang: trang (0-based), size (mặc định 3). status: ACTIVE | DRAFT | null (tất cả).
     * sortField: "price" | "stock" | "name" | null.
     * sortDir: "asc" | "desc" | null.
     */
    ProductListPageDto listProductsByShopId(Long shopId,
                                            String nameKeyword,
                                            BigDecimal minPrice,
                                            BigDecimal maxPrice,
                                            Long categoryId,
                                            String status,
                                            Integer page,
                                            Integer size,
                                            String sortField,
                                            String sortDir);

    /** Số lượng sản phẩm theo trạng thái (cho tab). */
    ProductSellerCountsDto getProductCountsByShop(Long shopId);

    ReadProductClientDto getProductDetailClient(Long productId);
    /** Danh sách sản phẩm thuộc category lá (cho trang category client). */
    List<ProductCardDto> listProductsByCategoryId(Long categoryId);
    List<ProductCardDto> listProductsByCategoryId(Long categoryId, BigDecimal minPrice, BigDecimal maxPrice);
    List<ProductCardDto> listProductsByCategoryId(Long categoryId, BigDecimal minPrice, BigDecimal maxPrice, List<Long> brandValueIds, String sortField, String sortDir);
    /** Phân trang: page (0-based), size. */
    ProductCardListPageDto listProductsByCategoryIdPaged(Long categoryId, BigDecimal minPrice, BigDecimal maxPrice, List<Long> brandValueIds, String sortField, String sortDir, int page, int size);
    /** Search đơn giản theo tên sản phẩm + filter khoảng giá. */
    List<ProductCardDto> searchProductsByName(String keyword, BigDecimal minPrice, BigDecimal maxPrice, String sortField, String sortDir);
    /** Danh sách brand (attribute value) cho nhánh category (bao gồm leaf con cháu). */
    List<ReadProductAttributeValueDto> listBrandsForCategoryBranch(Long categoryId);
    void updateProduct(ProductRequest productRequest, MultipartFile[] media);

    /**
     * Xóa mềm một sản phẩm thuộc shop của seller đang đăng nhập.
     * - Chỉ cho phép xóa sản phẩm thuộc shop của accountId.
     * - Thực hiện soft-delete product và toàn bộ variations / tiers / options / images / attributes liên quan.
     */
    void deleteMyProduct(Long accountId, Long productId);

    /**
     * Cập nhật trạng thái sản phẩm thuộc shop của seller đang đăng nhập.
     * Chỉ cho phép update field status (ACTIVE | DRAFT | INACTIVE).
     */
    void updateMyProductStatus(Long accountId, Long productId, String status);
}
