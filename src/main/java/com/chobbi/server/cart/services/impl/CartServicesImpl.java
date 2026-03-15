package com.chobbi.server.cart.services.impl;

import com.chobbi.server.account.entity.AccountEntity;
import com.chobbi.server.account.repo.AccountRepo;
import com.chobbi.server.cart.dto.AddCartRequest;
import com.chobbi.server.cart.dto.CartItemDto;
import com.chobbi.server.cart.dto.CartShopGroupDto;
import com.chobbi.server.cart.dto.GetCartResponseDto;
import com.chobbi.server.cart.dto.MiniCartItemDto;
import com.chobbi.server.cart.dto.MiniCartResponseDto;
import com.chobbi.server.cart.dto.VariationOptionDisplayDto;
import com.chobbi.server.cart.entity.CartEntity;
import com.chobbi.server.cart.entity.CartVariationEntity;
import com.chobbi.server.cart.repo.CartRepo;
import com.chobbi.server.cart.services.CartServices;
import com.chobbi.server.catalog.entity.VariationOptionEntity;
import com.chobbi.server.catalog.entity.VariationEntity;
import com.chobbi.server.catalog.repo.CartVariationRepo;
import com.chobbi.server.catalog.repo.VariationOptionRepo;
import com.chobbi.server.catalog.repo.VariationRepo;
import com.chobbi.server.inventory.services.InventoryServices;
import com.chobbi.server.shipping.dto.ShopProductIdsRequest;
import com.chobbi.server.shipping.dto.ShopShippingOptionsDto;
import com.chobbi.server.shipping.services.ShippingEstimateService;
import com.chobbi.server.shop.entity.ShopEntity;
import com.chobbi.server.catalog.enums.StatusEnums;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServicesImpl implements CartServices {
    private final InventoryServices inventoryServices;
    private final CartRepo cartRepo;
    private final AccountRepo accountRepo;
    private final VariationRepo variationRepo;
    private final CartVariationRepo cartVariationRepo;
    private final VariationOptionRepo variationOptionRepo;
    private final ShippingEstimateService shippingEstimateService;

    @Override
    public void addToCart(AddCartRequest req) {
        AccountEntity account = accountRepo.findById(req.getAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found"));
        CartEntity cart = cartRepo.findByAccountEntityId(account.getId())
                .orElseGet(() -> {
                    CartEntity cartEntity = new CartEntity();
                    cartEntity.setAccountEntity(account);
                    return cartEntity;
                });
        VariationEntity variation = variationRepo.findByIdAndDeletedAtIsNull(req.getVariationId())
                .orElseThrow(() -> new RuntimeException("Variation not found"));
        Integer currentStock = variation.getStock();
        BigDecimal priceAtTime = variation.getPrice();

        Optional<CartVariationEntity> existingCartVariation = cart.getCartVariations().stream()
                .filter(cv -> cv.getDeletedAt() == null
                        && cv.getVariationEntity() != null
                        && cv.getVariationEntity().getId().equals(variation.getId()))
                .findFirst();
        if (existingCartVariation.isPresent()) {
            CartVariationEntity item = existingCartVariation.get();
            Integer totalQuantity = item.getQuantity() + req.getQuantity();
            if (inventoryServices.isStockAvailable(currentStock, totalQuantity)) {
                item.setQuantity(totalQuantity);
            } else {
                throw new RuntimeException("Stock not enough");
            }
        } else {
            CartVariationEntity item = new CartVariationEntity();
            Integer quantity = req.getQuantity();
            if (inventoryServices.isStockAvailable(currentStock, quantity)) {
                item.setQuantity(quantity);
            } else {
                throw new RuntimeException("Stock not enough");
            }
            item.setCartEntity(cart);
            item.setVariationEntity(variation);
            item.setPriceAtTime(priceAtTime);
            cart.getCartVariations().add(item);
        }
        cartRepo.save(cart);
    }

    @Override
    public GetCartResponseDto getCartByAccountId(Long accountId) {
        List<CartVariationEntity> items = cartVariationRepo.findByAccountIdWithDetails(accountId);
        if (items.isEmpty()) {
            return new GetCartResponseDto(new ArrayList<>());
        }

        List<Long> variationIds = items.stream()
                .map(cv -> cv.getVariationEntity().getId())
                .distinct()
                .toList();
        List<VariationOptionEntity> optionEntities = variationOptionRepo.findByVariationEntity_IdInWithDetails(variationIds);
        Map<Long, List<VariationOptionDisplayDto>> variationOptionsMap = optionEntities.stream()
                .collect(Collectors.groupingBy(
                        vo -> vo.getVariationEntity().getId(),
                        Collectors.mapping(vo -> new VariationOptionDisplayDto(
                                vo.getOptionsEntity().getTierEntity().getName(),
                                vo.getOptionsEntity().getName()
                        ), Collectors.toList())
                ));

        Map<Long, CartShopGroupDto> shopMap = new LinkedHashMap<>();

        for (CartVariationEntity cv : items) {
            VariationEntity v = cv.getVariationEntity();
            if (v == null || v.getProductEntity() == null) continue;
            ShopEntity shop = v.getProductEntity().getShopEntity();
            if (shop == null) continue;

            List<VariationOptionDisplayDto> options = variationOptionsMap.getOrDefault(v.getId(), new ArrayList<>());

            Long productWeight = v.getProductEntity().getWeight() != null
                    ? v.getProductEntity().getWeight().longValue()
                    : 0L;

            boolean productDeleted = v.getProductEntity().getDeletedAt() != null;
            boolean variationDeleted = v.getDeletedAt() != null;
            boolean productInactive = v.getProductEntity().getStatus() != StatusEnums.ACTIVE;

            CartItemDto item = new CartItemDto();
            item.setCartVariationId(cv.getId());
            item.setVariationId(v.getId());
            item.setProductId(v.getProductEntity().getId());
            item.setProductName(v.getProductEntity().getName());
            item.setImageUrl(v.getProductEntity().getThumbnail());
            item.setQuantity(cv.getQuantity());
            // Nếu sản phẩm/biến thể đã xóa hoặc ngừng bán, vẫn hiển thị giá đã lưu trong giỏ,
            // nhưng item sẽ bị disabled nên không tham gia thanh toán.
            item.setPrice(variationDeleted || productDeleted || productInactive
                    ? cv.getPriceAtTime()
                    : v.getPrice());
            item.setWeight(productWeight);
            item.setVariationOptions(options);
            item.setDisabled(productDeleted || variationDeleted || productInactive);

            shopMap.computeIfAbsent(shop.getId(), id -> new CartShopGroupDto(shop.getId(), shop.getName(), new ArrayList<>(), new ArrayList<>()))
                    .getItems()
                    .add(item);
        }

        List<CartShopGroupDto> shopGroups = new ArrayList<>(shopMap.values());
        if (!shopGroups.isEmpty()) {
            List<ShopProductIdsRequest> estimateRequest = shopGroups.stream()
                    .map(g -> {
                        List<Long> productIds = g.getItems().stream()
                                .map(CartItemDto::getProductId)
                                .distinct()
                                .toList();
                        return new ShopProductIdsRequest(g.getShopId(), productIds);
                    })
                    .toList();
            try {
                List<ShopShippingOptionsDto> estimateResult = shippingEstimateService.estimateByShopProducts(estimateRequest);
                for (int i = 0; i < shopGroups.size() && i < estimateResult.size(); i++) {
                    shopGroups.get(i).setShippingOptions(estimateResult.get(i).getOptions());
                }
            } catch (Exception ignored) {
                // Giữ shippingOptions rỗng nếu estimate lỗi
            }
        }
        return new GetCartResponseDto(shopGroups);
    }

    @Override
    public MiniCartResponseDto getMiniCartByAccountId(Long accountId) {
        List<CartVariationEntity> items = cartVariationRepo.findByAccountIdWithDetails(accountId);
        if (items.isEmpty()) {
            return new MiniCartResponseDto(List.of(), 0);
        }
        // Chỉ đếm và hiển thị những item có product còn hoạt động
        List<CartVariationEntity> activeItems = items.stream()
                .filter(cv -> {
                    VariationEntity v = cv.getVariationEntity();
                    if (v == null || v.getProductEntity() == null) return false;
                    return v.getDeletedAt() == null
                            && v.getProductEntity().getDeletedAt() == null
                            && v.getProductEntity().getStatus() == StatusEnums.ACTIVE;
                })
                .toList();

        int totalItems = activeItems.size();
        List<MiniCartItemDto> miniItems = activeItems.stream()
                .limit(3)
                .map(cv -> {
                    VariationEntity v = cv.getVariationEntity();
                    if (v == null || v.getProductEntity() == null) {
                        return null;
                    }
                    return new MiniCartItemDto(
                            v.getProductEntity().getId(),
                            v.getProductEntity().getName(),
                            v.getProductEntity().getThumbnail(),
                            v.getPrice()
                    );
                })
                .filter(i -> i != null)
                .toList();
        return new MiniCartResponseDto(miniItems, totalItems);
    }

    @Override
    public void removeCartItem(Long accountId, Long cartVariationId) {
        CartVariationEntity item = cartVariationRepo.findById(cartVariationId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        CartEntity cart = item.getCartEntity();
        if (cart == null || cart.getAccountEntity() == null
                || !cart.getAccountEntity().getId().equals(accountId)) {
            throw new RuntimeException("Cart item does not belong to current account");
        }

        // Soft delete thông qua BaseEntity (deletedAt)
        item.softDelete();
        cartVariationRepo.save(item);
    }
}
