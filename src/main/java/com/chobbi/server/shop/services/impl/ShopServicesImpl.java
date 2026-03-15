package com.chobbi.server.shop.services.impl;

import com.chobbi.server.account.entity.AccountEntity;
import com.chobbi.server.account.repo.AccountRepo;
import com.chobbi.server.catalog.entity.ProductEntity;
import com.chobbi.server.catalog.entity.VariationEntity;
import com.chobbi.server.catalog.enums.StatusEnums;
import com.chobbi.server.catalog.repo.ProductRepo;
import com.chobbi.server.exception.BusinessException;
import com.chobbi.server.shop.dto.CreateShopCategoryRequest;
import com.chobbi.server.shop.dto.PublicShopCategoryRefDto;
import com.chobbi.server.shop.dto.PublicShopPageDto;
import com.chobbi.server.shop.dto.PublicShopProductDto;
import com.chobbi.server.shop.dto.PublicShopProductListPageDto;
import com.chobbi.server.shop.dto.PublicShopProfileDto;
import com.chobbi.server.shop.dto.ShopBannerDto;
import com.chobbi.server.shop.dto.ShopCategoryProductDto;
import com.chobbi.server.shop.dto.ShopCategoryResponse;
import com.chobbi.server.shop.dto.ShopResponse;
import com.chobbi.server.shop.entity.ShopBannerEntity;
import com.chobbi.server.shop.entity.ShopCategoryEntity;
import com.chobbi.server.shop.entity.ShopEntity;
import com.chobbi.server.shop.repo.ShopBannerRepo;
import com.chobbi.server.shop.repo.ShopCategoryRepo;
import com.chobbi.server.shop.repo.ShopRepo;
import com.chobbi.server.shop.services.ShopServices;
import com.chobbi.server.storage.services.FilesStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ShopServicesImpl implements ShopServices {

    private static final int MAX_BANNERS = 2;
    private static final String BANNER_FOLDER_PREFIX = "shop/";

    private final ShopRepo shopRepo;
    private final ShopCategoryRepo shopCategoryRepo;
    private final ShopBannerRepo shopBannerRepo;
    private final ProductRepo productRepo;
    private final AccountRepo accountRepo;
    private final FilesStorageService filesStorageService;

    @Override
    public Optional<ShopResponse> getMyShop(Long accountId) {
        return shopRepo.findByAccountEntity_IdAndDeletedAtIsNull(accountId)
                .map(this::toResponse);
    }

    @Override
    public ShopResponse createShop(Long accountId, String name) {
        if (name == null || name.isBlank()) {
            throw new BusinessException("Tên shop không được để trống", HttpStatus.BAD_REQUEST);
        }

        if (shopRepo.existsByAccountEntity_Id(accountId)) {
            throw new BusinessException("Tài khoản đã có shop", HttpStatus.BAD_REQUEST);
        }

        AccountEntity account = accountRepo.findById(accountId)
                .orElseThrow(() -> new BusinessException("Account not found", HttpStatus.NOT_FOUND));

        ShopEntity shop = new ShopEntity();
        shop.setName(name.trim());
        shop.setAccountEntity(account);

        ShopEntity saved = shopRepo.save(shop);
        return toResponse(saved);
    }

    @Override
    public ShopResponse updateShopInfo(Long accountId, String name, MultipartFile avatarFile) {
        ShopEntity shop = shopRepo.findByAccountEntity_IdAndDeletedAtIsNull(accountId)
                .orElseThrow(() -> new BusinessException("Tài khoản chưa có shop", HttpStatus.BAD_REQUEST));

        if (name != null && !name.isBlank()) {
            shop.setName(name.trim());
        }
        if (avatarFile != null && !avatarFile.isEmpty()) {
            String folder = BANNER_FOLDER_PREFIX + shop.getId() + "/avatar";
            String oldPath = shop.getAvatar();
            if (oldPath != null && !oldPath.isBlank()) {
                filesStorageService.deleteByRelativePath(oldPath);
            }
            String newPath = filesStorageService.transferToFolder(avatarFile, folder);
            filesStorageService.processOptimization(newPath);
            shop.setAvatar(newPath);
        }
        ShopEntity saved = shopRepo.save(shop);
        return toResponse(saved);
    }

    @Override
    public ShopCategoryResponse createShopCategory(Long accountId, CreateShopCategoryRequest request) {
        if (request == null || request.getName() == null || request.getName().isBlank()) {
            throw new BusinessException("Tên danh mục shop không được để trống", HttpStatus.BAD_REQUEST);
        }

        // Lấy shop của account hiện tại
        ShopEntity shop = shopRepo.findByAccountEntity_IdAndDeletedAtIsNull(accountId)
                .orElseThrow(() -> new BusinessException("Tài khoản chưa có shop", HttpStatus.BAD_REQUEST));

        ShopCategoryEntity category = new ShopCategoryEntity();
        category.setName(request.getName().trim());
        category.setShopEntity(shop);
        if (request.getSortOrder() != null) {
            category.setSortOrder(request.getSortOrder());
        }
        // Mặc định sau khi tạo là TẮT trừ khi truyền rõ ràng.
        if (request.getIsActive() != null) {
            category.setIsActive(request.getIsActive());
        } else {
            category.setIsActive(false);
        }

        // Lưu trước để có id
        ShopCategoryEntity savedCategory = shopCategoryRepo.save(category);

        // Gán product nếu có danh sách productIds
        List<Long> productIds = request.getProductIds() != null ? request.getProductIds() : Collections.emptyList();
        if (!productIds.isEmpty()) {
            List<ProductEntity> products = productRepo.findByIdInAndShopEntity_IdAndDeletedAtIsNull(productIds, shop.getId());

            if (products.size() != productIds.size()) {
                throw new BusinessException("Một số sản phẩm không tồn tại hoặc không thuộc shop hiện tại", HttpStatus.BAD_REQUEST);
            }

            for (ProductEntity product : products) {
                product.setShopCategory(savedCategory);
            }
            productRepo.saveAll(products);
        }

        List<ProductEntity> productsForResponse =
                productRepo.findByShopCategory_IdAndDeletedAtIsNullWithVariations(savedCategory.getId());
        return toCategoryResponse(savedCategory, productsForResponse);
    }

    @Override
    public java.util.List<ShopCategoryResponse> listMyShopCategories(Long accountId) {
        ShopEntity shop = shopRepo.findByAccountEntity_IdAndDeletedAtIsNull(accountId)
                .orElseThrow(() -> new BusinessException("Tài khoản chưa có shop", HttpStatus.BAD_REQUEST));

        List<ShopCategoryEntity> categories =
                shopCategoryRepo.findByShopEntity_IdAndDeletedAtIsNullOrderBySortOrderAscIdAsc(shop.getId());

        return categories.stream()
                .map(cat -> {
                    List<ProductEntity> products =
                            productRepo.findByShopCategory_IdAndDeletedAtIsNullWithVariations(cat.getId());
                    return toCategoryResponse(cat, products);
                })
                .toList();
    }

    @Override
    public ShopCategoryResponse updateShopCategoryStatus(Long accountId, Long categoryId, boolean isActive) {
        ShopEntity shop = shopRepo.findByAccountEntity_IdAndDeletedAtIsNull(accountId)
                .orElseThrow(() -> new BusinessException("Tài khoản chưa có shop", HttpStatus.BAD_REQUEST));

        ShopCategoryEntity category = shopCategoryRepo
                .findByIdAndShopEntity_IdAndDeletedAtIsNull(categoryId, shop.getId())
                .orElseThrow(() -> new BusinessException("Danh mục không tồn tại hoặc không thuộc shop của bạn", HttpStatus.NOT_FOUND));

        category.setIsActive(isActive);
        ShopCategoryEntity saved = shopCategoryRepo.save(category);

        List<ProductEntity> products =
                productRepo.findByShopCategory_IdAndDeletedAtIsNullWithVariations(saved.getId());

        return toCategoryResponse(saved, products);
    }

    @Override
    public ShopCategoryResponse updateShopCategoryProducts(Long accountId, Long categoryId, List<Long> productIds) {
        ShopEntity shop = shopRepo.findByAccountEntity_IdAndDeletedAtIsNull(accountId)
                .orElseThrow(() -> new BusinessException("Tài khoản chưa có shop", HttpStatus.BAD_REQUEST));

        ShopCategoryEntity category = shopCategoryRepo
                .findByIdAndShopEntity_IdAndDeletedAtIsNull(categoryId, shop.getId())
                .orElseThrow(() -> new BusinessException("Danh mục không tồn tại hoặc không thuộc shop của bạn", HttpStatus.NOT_FOUND));

        Set<Long> newIds = productIds == null ? Set.of() : new HashSet<>(productIds);

        List<ProductEntity> currentInCategory =
                productRepo.findByShopCategory_IdAndDeletedAtIsNull(category.getId());
        for (ProductEntity p : currentInCategory) {
            if (!newIds.contains(p.getId())) {
                p.setShopCategory(null);
            }
        }
        productRepo.saveAll(currentInCategory);

        if (!newIds.isEmpty()) {
            List<ProductEntity> toAssign =
                    productRepo.findByIdInAndShopEntity_IdAndDeletedAtIsNull(List.copyOf(newIds), shop.getId());
            for (ProductEntity p : toAssign) {
                p.setShopCategory(category);
            }
            productRepo.saveAll(toAssign);
        }

        List<ProductEntity> updatedProducts =
                productRepo.findByShopCategory_IdAndDeletedAtIsNullWithVariations(category.getId());
        return toCategoryResponse(category, updatedProducts);
    }

    @Override
    public ShopCategoryResponse updateShopCategoryName(Long accountId, Long categoryId, String name) {
        ShopEntity shop = shopRepo.findByAccountEntity_IdAndDeletedAtIsNull(accountId)
                .orElseThrow(() -> new BusinessException("Tài khoản chưa có shop", HttpStatus.BAD_REQUEST));

        ShopCategoryEntity category = shopCategoryRepo
                .findByIdAndShopEntity_IdAndDeletedAtIsNull(categoryId, shop.getId())
                .orElseThrow(() -> new BusinessException("Danh mục không tồn tại hoặc không thuộc shop của bạn", HttpStatus.NOT_FOUND));

        if (name == null || name.isBlank()) {
            throw new BusinessException("Tên danh mục không được để trống", HttpStatus.BAD_REQUEST);
        }
        category.setName(name.trim());
        ShopCategoryEntity saved = shopCategoryRepo.save(category);

        List<ProductEntity> products =
                productRepo.findByShopCategory_IdAndDeletedAtIsNullWithVariations(saved.getId());
        return toCategoryResponse(saved, products);
    }

    @Override
    public java.util.List<ShopCategoryResponse> updateShopCategoryOrder(Long accountId, java.util.List<Long> categoryIdsInOrder) {
        ShopEntity shop = shopRepo.findByAccountEntity_IdAndDeletedAtIsNull(accountId)
                .orElseThrow(() -> new BusinessException("Tài khoản chưa có shop", HttpStatus.BAD_REQUEST));

        if (categoryIdsInOrder == null || categoryIdsInOrder.isEmpty()) {
            return listMyShopCategories(accountId);
        }
        for (int i = 0; i < categoryIdsInOrder.size(); i++) {
            Long catId = categoryIdsInOrder.get(i);
            ShopCategoryEntity category = shopCategoryRepo
                    .findByIdAndShopEntity_IdAndDeletedAtIsNull(catId, shop.getId())
                    .orElseThrow(() -> new BusinessException("Danh mục không tồn tại hoặc không thuộc shop của bạn", HttpStatus.NOT_FOUND));
            category.setSortOrder(i);
            shopCategoryRepo.save(category);
        }
        return listMyShopCategories(accountId);
    }

    @Override
    public List<ShopBannerDto> listBanners(Long accountId) {
        ShopEntity shop = shopRepo.findByAccountEntity_IdAndDeletedAtIsNull(accountId)
                .orElseThrow(() -> new BusinessException("Tài khoản chưa có shop", HttpStatus.BAD_REQUEST));
        return shopBannerRepo.findByShopEntity_IdAndDeletedAtIsNullOrderBySortOrderAscIdAsc(shop.getId())
                .stream()
                .map(this::toBannerDto)
                .toList();
    }

    @Override
    public ShopBannerDto addBanner(Long accountId, MultipartFile file) {
        ShopEntity shop = shopRepo.findByAccountEntity_IdAndDeletedAtIsNull(accountId)
                .orElseThrow(() -> new BusinessException("Tài khoản chưa có shop", HttpStatus.BAD_REQUEST));

        long count = shopBannerRepo.countByShopEntity_IdAndDeletedAtIsNull(shop.getId());
        if (count >= MAX_BANNERS) {
            throw new BusinessException("Tối đa " + MAX_BANNERS + " banner. Vui lòng xóa bớt trước khi thêm.", HttpStatus.BAD_REQUEST);
        }
        if (file == null || file.isEmpty()) {
            throw new BusinessException("File ảnh không được để trống", HttpStatus.BAD_REQUEST);
        }

        String folder = BANNER_FOLDER_PREFIX + shop.getId() + "/banner";
        String imagePath = filesStorageService.transferToFolder(file, folder);
        filesStorageService.processOptimization(imagePath);

        ShopBannerEntity banner = new ShopBannerEntity();
        banner.setShopEntity(shop);
        banner.setImagePath(imagePath);
        banner.setSortOrder((int) count);
        banner.setIsActive(true);
        ShopBannerEntity saved = shopBannerRepo.save(banner);
        return toBannerDto(saved);
    }

    @Override
    public void deleteBanner(Long accountId, Long bannerId) {
        ShopEntity shop = shopRepo.findByAccountEntity_IdAndDeletedAtIsNull(accountId)
                .orElseThrow(() -> new BusinessException("Tài khoản chưa có shop", HttpStatus.BAD_REQUEST));

        ShopBannerEntity banner = shopBannerRepo.findByIdAndShopEntity_IdAndDeletedAtIsNull(bannerId, shop.getId())
                .orElseThrow(() -> new BusinessException("Banner không tồn tại hoặc không thuộc shop của bạn", HttpStatus.NOT_FOUND));

        filesStorageService.deleteByRelativePath(banner.getImagePath());
        banner.softDelete();
        shopBannerRepo.save(banner);
    }

    @Override
    public List<ShopBannerDto> updateBannerOrder(Long accountId, List<Long> bannerIdsInOrder) {
        ShopEntity shop = shopRepo.findByAccountEntity_IdAndDeletedAtIsNull(accountId)
                .orElseThrow(() -> new BusinessException("Tài khoản chưa có shop", HttpStatus.BAD_REQUEST));

        if (bannerIdsInOrder == null || bannerIdsInOrder.isEmpty()) {
            return listBanners(accountId);
        }
        for (int i = 0; i < bannerIdsInOrder.size(); i++) {
            Long bannerId = bannerIdsInOrder.get(i);
            ShopBannerEntity banner = shopBannerRepo.findByIdAndShopEntity_IdAndDeletedAtIsNull(bannerId, shop.getId())
                    .orElseThrow(() -> new BusinessException("Banner không tồn tại hoặc không thuộc shop của bạn", HttpStatus.NOT_FOUND));
            banner.setSortOrder(i);
            shopBannerRepo.save(banner);
        }
        return listBanners(accountId);
    }

    @Override
    public PublicShopPageDto getPublicShopPage(Long shopId) {
        ShopEntity shop = shopRepo.findByIdAndDeletedAtIsNull(shopId)
                .orElseThrow(() -> new BusinessException("Shop không tồn tại", HttpStatus.NOT_FOUND));

        if (shop.getStatus() != StatusEnums.ACTIVE) {
            throw new BusinessException("Shop không tồn tại", HttpStatus.NOT_FOUND);
        }

        PublicShopProfileDto profile = PublicShopProfileDto.builder()
                .id(shop.getId())
                .name(shop.getName())
                .avatar(shop.getAvatar())
                .build();

        List<ShopCategoryEntity> allCategories =
                shopCategoryRepo.findByShopEntity_IdAndDeletedAtIsNullOrderBySortOrderAscIdAsc(shop.getId());
        List<PublicShopCategoryRefDto> shopCategories = allCategories.stream()
                .filter(cat -> Boolean.TRUE.equals(cat.getIsActive()))
                .map(cat -> PublicShopCategoryRefDto.builder()
                        .id(cat.getId())
                        .name(cat.getName())
                        .build())
                .toList();

        List<ShopBannerDto> banners = shopBannerRepo
                .findByShopEntity_IdAndDeletedAtIsNullOrderBySortOrderAscIdAsc(shop.getId())
                .stream()
                .map(this::toBannerDto)
                .toList();

        List<ProductEntity> allProducts =
                productRepo.findByShopEntity_IdAndDeletedAtIsNullWithVariations(shop.getId());
        List<PublicShopProductDto> products = allProducts.stream()
                .filter(p -> p.getStatus() == StatusEnums.ACTIVE)
                .map(this::toPublicShopProductDto)
                .toList();

        return PublicShopPageDto.builder()
                .profile(profile)
                .banners(banners)
                .shopCategories(shopCategories)
                .products(products)
                .build();
    }

    @Override
    public PublicShopProductListPageDto getPublicShopProducts(Long shopId, Long shopCategoryId, int page, int size) {
        ShopEntity shop = shopRepo.findByIdAndDeletedAtIsNull(shopId)
                .orElseThrow(() -> new BusinessException("Shop không tồn tại", HttpStatus.NOT_FOUND));

        if (shop.getStatus() != StatusEnums.ACTIVE) {
            throw new BusinessException("Shop không tồn tại", HttpStatus.NOT_FOUND);
        }

        List<ProductEntity> allProducts =
                productRepo.findByShopEntity_IdAndDeletedAtIsNullWithVariations(shop.getId());

        // Chỉ lấy sản phẩm ACTIVE
        List<ProductEntity> activeProducts = allProducts.stream()
                .filter(p -> p.getStatus() == StatusEnums.ACTIVE)
                .toList();

        // Lọc theo danh mục shop (nếu có)
        List<ProductEntity> filtered = activeProducts;
        if (shopCategoryId != null) {
            filtered = activeProducts.stream()
                    .filter(p -> p.getShopCategory() != null && shopCategoryId.equals(p.getShopCategory().getId()))
                    .toList();
        }

        List<PublicShopProductDto> full = filtered.stream()
                .map(this::toPublicShopProductDto)
                .toList();

        long totalElements = full.size();
        int safeSize = size <= 0 ? 12 : size;
        int totalPages = totalElements == 0 ? 0 : (int) Math.ceil((double) totalElements / safeSize);
        int safePage = Math.max(0, Math.min(page, totalPages == 0 ? 0 : totalPages - 1));
        int from = safePage * safeSize;
        int to = (int) Math.min(from + safeSize, totalElements);
        List<PublicShopProductDto> content = from < totalElements ? full.subList(from, to) : List.of();

        return new PublicShopProductListPageDto(content, totalPages, totalElements, safePage, safeSize);
    }

    private PublicShopProductDto toPublicShopProductDto(ProductEntity p) {
        List<VariationEntity> activeVariations = p.getVariations() == null ? List.of()
                : p.getVariations().stream()
                .filter(v -> v.getDeletedAt() == null)
                .toList();
        BigDecimal minPrice = activeVariations.stream()
                .map(VariationEntity::getPrice)
                .filter(price -> price != null)
                .min(BigDecimal::compareTo)
                .orElse(null);
        BigDecimal maxPrice = activeVariations.stream()
                .map(VariationEntity::getPrice)
                .filter(price -> price != null)
                .max(BigDecimal::compareTo)
                .orElse(null);
        int totalStock = activeVariations.stream()
                .mapToInt(v -> v.getStock() != null ? v.getStock() : 0)
                .sum();
        Long shopCategoryId = p.getShopCategory() != null ? p.getShopCategory().getId() : null;
        return PublicShopProductDto.builder()
                .id(p.getId())
                .name(p.getName())
                .thumbnail(p.getThumbnail())
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .totalStock(totalStock)
                .shopCategoryId(shopCategoryId)
                .build();
    }

    private ShopBannerDto toBannerDto(ShopBannerEntity b) {
        return ShopBannerDto.builder()
                .id(b.getId())
                .imagePath(b.getImagePath())
                .sortOrder(b.getSortOrder())
                .build();
    }

    private ShopResponse toResponse(ShopEntity shop) {
        return ShopResponse.builder()
                .id(shop.getId())
                .name(shop.getName())
                .avatar(shop.getAvatar())
                .status(shop.getStatus())
                .build();
    }

    private ShopCategoryResponse toCategoryResponse(ShopCategoryEntity category,
                                                    List<ProductEntity> products) {
        List<ShopCategoryProductDto> productDtos = products == null ? Collections.emptyList()
                : products.stream()
                .map(p -> {
                    List<VariationEntity> activeVariations = p.getVariations() == null ? List.of()
                            : p.getVariations().stream()
                            .filter(v -> v.getDeletedAt() == null)
                            .toList();
                    BigDecimal minPrice = activeVariations.stream()
                            .map(VariationEntity::getPrice)
                            .filter(price -> price != null)
                            .min(BigDecimal::compareTo)
                            .orElse(null);
                    int totalStock = activeVariations.stream()
                            .mapToInt(v -> v.getStock() != null ? v.getStock() : 0)
                            .sum();
                    return new ShopCategoryProductDto(
                            p.getId(),
                            p.getName(),
                            p.getThumbnail(),
                            minPrice,
                            totalStock
                    );
                })
                .toList();

        return ShopCategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .sortOrder(category.getSortOrder())
                .isActive(category.getIsActive())
                .shopId(category.getShopEntity() != null ? category.getShopEntity().getId() : null)
                .products(productDtos)
                .build();
    }
}

