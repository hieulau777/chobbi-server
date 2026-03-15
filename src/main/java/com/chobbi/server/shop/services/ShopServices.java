package com.chobbi.server.shop.services;

import com.chobbi.server.shop.dto.PublicShopPageDto;
import com.chobbi.server.shop.dto.PublicShopProductListPageDto;
import com.chobbi.server.shop.dto.ShopBannerDto;
import com.chobbi.server.shop.dto.ShopCategoryResponse;
import com.chobbi.server.shop.dto.CreateShopCategoryRequest;
import com.chobbi.server.shop.dto.ShopResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface ShopServices {

    Optional<ShopResponse> getMyShop(Long accountId);

    ShopResponse createShop(Long accountId, String name);

    /** Cập nhật tên và/hoặc avatar shop. name null/blank = giữ nguyên; avatar null = giữ nguyên. */
    ShopResponse updateShopInfo(Long accountId, String name, MultipartFile avatarFile);

    ShopCategoryResponse createShopCategory(Long accountId, CreateShopCategoryRequest request);

    /** Danh sách danh mục shop + sản phẩm (nếu có) của seller hiện tại. */
    List<ShopCategoryResponse> listMyShopCategories(Long accountId);

    /** Cập nhật trạng thái bật/tắt danh mục shop. */
    ShopCategoryResponse updateShopCategoryStatus(Long accountId, Long categoryId, boolean isActive);

    /** Full update: thay thế toàn bộ sản phẩm thuộc danh mục shop bằng danh sách productIds. */
    ShopCategoryResponse updateShopCategoryProducts(Long accountId, Long categoryId, List<Long> productIds);

    /** Cập nhật tên danh mục shop. */
    ShopCategoryResponse updateShopCategoryName(Long accountId, Long categoryId, String name);

    /** Cập nhật thứ tự danh mục shop. Body: list categoryId theo thứ tự mong muốn (index 0 = sortOrder 0). */
    List<ShopCategoryResponse> updateShopCategoryOrder(Long accountId, List<Long> categoryIdsInOrder);

    /** Danh sách banner của shop (tối đa 2). */
    List<ShopBannerDto> listBanners(Long accountId);

    /** Upload banner. Tối đa 2 banner. Lưu vào shop/{shopId}/banner. */
    ShopBannerDto addBanner(Long accountId, MultipartFile file);

    /** Xóa banner (soft delete + xóa file). */
    void deleteBanner(Long accountId, Long bannerId);

    /** Cập nhật thứ tự banner. Body: list bannerId theo thứ tự. */
    List<ShopBannerDto> updateBannerOrder(Long accountId, List<Long> bannerIdsInOrder);

    /** API public: lấy thông tin shop + danh mục + sản phẩm (chỉ danh mục bật, sản phẩm ACTIVE). */
    PublicShopPageDto getPublicShopPage(Long shopId);

    /**
     * API public: danh sách sản phẩm ACTIVE của shop (tất cả hoặc theo danh mục con),
     * phân trang page (0-based), size.
     */
    PublicShopProductListPageDto getPublicShopProducts(Long shopId, Long shopCategoryId, int page, int size);
}

