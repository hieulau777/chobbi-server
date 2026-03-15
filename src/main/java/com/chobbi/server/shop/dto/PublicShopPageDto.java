package com.chobbi.server.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class PublicShopPageDto {
    /** Thông tin shop (id, tên, avatar). */
    private PublicShopProfileDto profile;
    /** Banner của shop (theo sortOrder). */
    private List<ShopBannerDto> banners;
    /** Danh mục con của shop (id + tên), chỉ danh mục đang bật. Product tham chiếu qua shop_category_id. */
    private List<PublicShopCategoryRefDto> shopCategories;
    /** Tất cả sản phẩm ACTIVE của shop; mỗi product có shop_category_id trỏ vào 1 phần tử trong shopCategories. */
    private List<PublicShopProductDto> products;
}
