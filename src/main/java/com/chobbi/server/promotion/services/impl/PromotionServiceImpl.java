package com.chobbi.server.promotion.services.impl;

import com.chobbi.server.catalog.entity.ProductEntity;
import com.chobbi.server.catalog.entity.VariationEntity;
import com.chobbi.server.catalog.repo.ProductRepo;
import com.chobbi.server.exception.BusinessException;
import com.chobbi.server.promotion.dto.CreatePromotionRequest;
import com.chobbi.server.promotion.dto.ShopPromotionProductDto;
import com.chobbi.server.promotion.dto.ShopPromotionResponse;
import com.chobbi.server.promotion.entity.PromotionEntity;
import com.chobbi.server.promotion.entity.PromotionProductEntity;
import com.chobbi.server.promotion.repo.PromotionProductRepo;
import com.chobbi.server.promotion.repo.PromotionRepo;
import com.chobbi.server.promotion.services.PromotionService;
import com.chobbi.server.shop.entity.ShopEntity;
import com.chobbi.server.shop.repo.ShopRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {

    private final ShopRepo shopRepo;
    private final ProductRepo productRepo;
    private final PromotionRepo promotionRepo;
    private final PromotionProductRepo promotionProductRepo;

    @Override
    @Transactional
    public void createPromotionWithProducts(Long accountId, CreatePromotionRequest request) {
        if (request == null) {
            throw new BusinessException("Dữ liệu promotion không hợp lệ", HttpStatus.BAD_REQUEST);
        }
        if (request.getName() == null || request.getName().isBlank()) {
            throw new BusinessException("Tên promotion không được để trống", HttpStatus.BAD_REQUEST);
        }
        LocalDateTime startAt = request.getStartAt();
        LocalDateTime endAt = request.getEndAt();
        if (startAt == null || endAt == null) {
            throw new BusinessException("Thời gian bắt đầu / kết thúc không được để trống", HttpStatus.BAD_REQUEST);
        }
        if (!endAt.isAfter(startAt)) {
            throw new BusinessException("Thời gian kết thúc phải sau thời gian bắt đầu", HttpStatus.BAD_REQUEST);
        }

        // Xác định shop từ account hiện tại (mỗi account 1 shop)
        ShopEntity shop = shopRepo.findByAccountEntity_IdAndDeletedAtIsNull(accountId)
                .orElseThrow(() -> new BusinessException("Tài khoản chưa có shop", HttpStatus.BAD_REQUEST));

        // Load products thuộc shop
        List<Long> productIds = request.getProductIds() != null ? request.getProductIds() : List.of();
        List<ProductEntity> products = productIds.isEmpty()
                ? List.of()
                : productRepo.findByIdInAndShopEntity_IdAndDeletedAtIsNull(productIds, shop.getId());

        if (products.size() != productIds.size()) {
            throw new BusinessException("Một số sản phẩm không tồn tại hoặc không thuộc shop hiện tại", HttpStatus.BAD_REQUEST);
        }

        // Rule: 1 product không được nằm trong 2 campaign cùng thời gian hiệu lực
        for (ProductEntity product : products) {
            boolean hasOverlap = !promotionProductRepo
                    .findByProductEntity_IdAndPromotionEntity_DeletedAtIsNullAndPromotionEntity_StartAtLessThanEqualAndPromotionEntity_EndAtGreaterThanEqual(
                            product.getId(),
                            endAt,
                            startAt
                    )
                    .isEmpty();
            if (hasOverlap) {
                throw new BusinessException(
                        "Sản phẩm ID " + product.getId() + " đang nằm trong một campaign khác trong khoảng thời gian này",
                        HttpStatus.BAD_REQUEST
                );
            }
        }

        PromotionEntity promotion = new PromotionEntity();
        promotion.setShopEntity(shop);
        promotion.setName(request.getName().trim());
        promotion.setStartAt(startAt);
        promotion.setEndAt(endAt);

        PromotionEntity savedPromotion = promotionRepo.save(promotion);

        if (!products.isEmpty()) {
            List<PromotionProductEntity> links = new ArrayList<>();
            for (ProductEntity product : products) {
                PromotionProductEntity link = new PromotionProductEntity();
                link.setPromotionEntity(savedPromotion);
                link.setProductEntity(product);
                links.add(link);
            }
            promotionProductRepo.saveAll(links);
        }
    }

    @Override
    @Transactional
    public void updatePromotionWithProducts(Long accountId, Long promotionId, CreatePromotionRequest request) {
        if (request == null) {
            throw new BusinessException("Dữ liệu promotion không hợp lệ", HttpStatus.BAD_REQUEST);
        }
        if (request.getName() == null || request.getName().isBlank()) {
            throw new BusinessException("Tên promotion không được để trống", HttpStatus.BAD_REQUEST);
        }
        LocalDateTime startAt = request.getStartAt();
        LocalDateTime endAt = request.getEndAt();
        if (startAt == null || endAt == null) {
            throw new BusinessException("Thời gian bắt đầu / kết thúc không được để trống", HttpStatus.BAD_REQUEST);
        }
        if (!endAt.isAfter(startAt)) {
            throw new BusinessException("Thời gian kết thúc phải sau thời gian bắt đầu", HttpStatus.BAD_REQUEST);
        }

        ShopEntity shop = shopRepo.findByAccountEntity_IdAndDeletedAtIsNull(accountId)
                .orElseThrow(() -> new BusinessException("Tài khoản chưa có shop", HttpStatus.BAD_REQUEST));

        PromotionEntity promotion = promotionRepo.findByIdAndShopEntity_IdAndDeletedAtIsNull(promotionId, shop.getId())
                .orElseThrow(() -> new BusinessException("Campaign không tồn tại hoặc không thuộc shop của bạn", HttpStatus.NOT_FOUND));

        List<Long> productIds = request.getProductIds() != null ? request.getProductIds() : List.of();
        List<ProductEntity> products = productIds.isEmpty()
                ? List.of()
                : productRepo.findByIdInAndShopEntity_IdAndDeletedAtIsNull(productIds, shop.getId());

        if (products.size() != productIds.size()) {
            throw new BusinessException("Một số sản phẩm không tồn tại hoặc không thuộc shop hiện tại", HttpStatus.BAD_REQUEST);
        }

        for (ProductEntity product : products) {
            List<PromotionProductEntity> overlaps = promotionProductRepo
                    .findByProductEntity_IdAndPromotionEntity_DeletedAtIsNullAndPromotionEntity_StartAtLessThanEqualAndPromotionEntity_EndAtGreaterThanEqual(
                            product.getId(),
                            endAt,
                            startAt
                    );
            boolean hasOtherOverlap = overlaps.stream()
                    .anyMatch(link -> link.getPromotionEntity() != null
                            && !link.getPromotionEntity().getId().equals(promotionId));
            if (hasOtherOverlap) {
                throw new BusinessException(
                        "Sản phẩm ID " + product.getId() + " đang nằm trong một campaign khác trong khoảng thời gian này",
                        HttpStatus.BAD_REQUEST
                );
            }
        }

        promotion.setName(request.getName().trim());
        promotion.setStartAt(startAt);
        promotion.setEndAt(endAt);

        promotionProductRepo.deleteByPromotionEntity_Id(promotionId);

        if (!products.isEmpty()) {
            List<PromotionProductEntity> links = new ArrayList<>();
            for (ProductEntity product : products) {
                PromotionProductEntity link = new PromotionProductEntity();
                link.setPromotionEntity(promotion);
                link.setProductEntity(product);
                links.add(link);
            }
            promotionProductRepo.saveAll(links);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShopPromotionResponse> listMyPromotions(Long accountId) {
        ShopEntity shop = shopRepo.findByAccountEntity_IdAndDeletedAtIsNull(accountId)
                .orElseThrow(() -> new BusinessException("Tài khoản chưa có shop", HttpStatus.BAD_REQUEST));

        List<PromotionEntity> promotions = promotionRepo.findByShopEntity_IdAndDeletedAtIsNull(shop.getId());
        if (promotions == null || promotions.isEmpty()) {
            return Collections.emptyList();
        }

        LocalDateTime now = LocalDateTime.now();

        // Sắp xếp promotion: mới nhất lên trước (theo startAt, rồi id)
        promotions.sort(Comparator
                .comparing(PromotionEntity::getStartAt, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(PromotionEntity::getId)
                .reversed());

        List<ShopPromotionResponse> result = new ArrayList<>();
        for (PromotionEntity promo : promotions) {
            boolean active = (promo.getStartAt() != null && promo.getEndAt() != null)
                    && !promo.getStartAt().isAfter(now)
                    && !promo.getEndAt().isBefore(now);

            List<ShopPromotionProductDto> productDtos;
            List<PromotionProductEntity> links = promo.getPromotionProducts();
            if (links == null || links.isEmpty()) {
                productDtos = Collections.emptyList();
            } else {
                productDtos = links.stream()
                        .filter(link -> link.getDeletedAt() == null)
                        .map(PromotionProductEntity::getProductEntity)
                        .filter(p -> p != null && p.getDeletedAt() == null)
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
                            return new ShopPromotionProductDto(
                                    p.getId(),
                                    p.getName(),
                                    p.getThumbnail(),
                                    minPrice,
                                    totalStock
                            );
                        })
                        .toList();
            }

            ShopPromotionResponse dto = ShopPromotionResponse.builder()
                    .id(promo.getId())
                    .shopId(shop.getId())
                    .name(promo.getName())
                    .startAt(promo.getStartAt())
                    .endAt(promo.getEndAt())
                    .active(active)
                    .products(productDtos)
                    .build();
            result.add(dto);
        }

        return result;
    }
}

