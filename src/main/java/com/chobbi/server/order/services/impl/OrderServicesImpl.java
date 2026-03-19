package com.chobbi.server.order.services.impl;

import com.chobbi.server.account.entity.AccountEntity;
import com.chobbi.server.account.repo.AccountRepo;
import com.chobbi.server.address.entity.AddressEntity;
import com.chobbi.server.address.repo.AddressRepo;
import com.chobbi.server.cart.entity.CartVariationEntity;
import com.chobbi.server.cart.repo.CartRepo;
import com.chobbi.server.catalog.entity.ProductEntity;
import com.chobbi.server.catalog.entity.ProductImagesEntity;
import com.chobbi.server.catalog.entity.VariationEntity;
import com.chobbi.server.catalog.repo.ProductRepo;
import com.chobbi.server.shop.repo.ShopRepo;
import com.chobbi.server.catalog.repo.VariationRepo;
import com.chobbi.server.order.dto.*;
import com.chobbi.server.order.entity.OrderEntity;
import com.chobbi.server.order.entity.OrderGroupEntity;
import com.chobbi.server.order.entity.OrderVariationEntity;
import com.chobbi.server.order.repo.OrderGroupRepo;
import com.chobbi.server.order.repo.OrderRepo;
import com.chobbi.server.notification.services.NotificationServices;
import com.chobbi.server.order.services.OrderServices;
import com.chobbi.server.exception.BusinessException;
import com.chobbi.server.inventory.services.InventoryServices;
import com.chobbi.server.shipping.entity.ShippingEntity;
import com.chobbi.server.shipping.repo.ShippingRepo;
import com.chobbi.server.shop.entity.ShopEntity;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServicesImpl implements OrderServices {

    private final ShopRepo shopRepo;
    private final VariationRepo variationRepo;
    private final ProductRepo productRepo;
    private final ShippingRepo shippingRepo;
    private final AccountRepo accountRepo;
    private final OrderGroupRepo orderGroupRepo;
    private final OrderRepo orderRepo;
    private final InventoryServices inventoryServices;
    private final CartRepo cartRepo;
    private final NotificationServices notificationServices;
    private final AddressRepo addressRepo;

    @Override
    @Transactional
    public PlaceOrderResponse placeOrder(Long accountId, List<OrderRequest> req) {
        AccountEntity account = accountRepo.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại"));
        if (req == null || req.isEmpty()) {
            throw new BusinessException("Danh sách đơn hàng trống", HttpStatus.BAD_REQUEST);
        }

        Long addressId = req.getFirst().getAddressId();
        if (addressId == null) {
            throw new BusinessException("Vui lòng chọn địa chỉ nhận hàng", HttpStatus.BAD_REQUEST);
        }
        for (OrderRequest r : req) {
            if (r.getAddressId() == null || !r.getAddressId().equals(addressId)) {
                throw new BusinessException("Địa chỉ nhận hàng không hợp lệ", HttpStatus.BAD_REQUEST);
            }
        }

        AddressEntity address = addressRepo.findByIdAndAccountEntity_IdAndDeletedAtIsNull(addressId, accountId)
                .orElseThrow(() -> new BusinessException("Địa chỉ nhận hàng không tồn tại", HttpStatus.BAD_REQUEST));

        PreparedOrderGroup orderGroup = validateAndPrepareOrderGroup(req);
        return saveOrders(orderGroup, account, address);
    }

    private PreparedOrderGroup validateAndPrepareOrderGroup(List<OrderRequest> req) {
        PreparedOrderGroup orderGroup = new PreparedOrderGroup();
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal subTotalAll = BigDecimal.ZERO;
        Set<Long> shopIdSet = new HashSet<>();
        for (OrderRequest order : req) {
            if (!shopIdSet.add(order.getShopId())) {
                throw new RuntimeException("Duplicate shop id");
            }
            ShopEntity shopEntity = shopRepo.findByIdAndDeletedAtIsNull(order.getShopId())
                    .orElseThrow(() -> new RuntimeException("Shop id không hợp lệ"));
            ShippingEntity shippingEntity = shippingRepo.findByIdAndDeletedAtIsNull(order.getShippingId())
                    .orElseThrow(() -> new RuntimeException("Shipping id không hợp lệ"));

            PreparedOrderShop orderShop = new PreparedOrderShop();
            orderShop.setShopEntity(shopEntity);
            orderShop.setShippingEntity(shippingEntity);
            BigDecimal orderShopSubTotal = BigDecimal.ZERO;

            Set<Long> variationIdSet = new HashSet<>();
            for (OrderVariationDto variation: order.getVariations()) {
                Long id = variation.getVariationId();
                if (!variationIdSet.add(id)) {
                    throw new RuntimeException("Duplicate variation id");
                }
                VariationEntity variationEntity = variationRepo.findByIdAndDeletedAtIsNull(id)
                        .orElseThrow(() -> new RuntimeException("Variation id không tồn tại"));
                ProductEntity productEntity = productRepo.findByVariations_IdAndDeletedAtIsNull(id)
                        .orElseThrow(() -> new RuntimeException("Variation id không thuộc product"));
                ShopEntity checkShop = shopRepo.findByProducts_IdAndDeletedAtIsNull(productEntity.getId())
                        .orElseThrow(() -> new RuntimeException("Variation id không thuộc shop"));
                if (!order.getShopId().equals(checkShop.getId())) {
                    throw new RuntimeException("Shop id không hợp lệ 2");
                }

                inventoryServices.validateQuantityAvailable(id, variation.getQuantity());

                PreparedOrderVariations orderVariation = new PreparedOrderVariations();
                orderVariation.setVariation(variationEntity);
                orderVariation.setQuantity(variation.getQuantity());
                orderVariation.setPriceAtPoint(variationEntity.getPrice());

                BigDecimal orderVariationPrice = orderVariation.getPriceAtPoint();
                BigDecimal orderVariationQuantity = BigDecimal.valueOf(orderVariation.getQuantity());
                BigDecimal orderVariationSubTotal = orderVariationPrice.multiply(orderVariationQuantity);
                orderShopSubTotal = orderShopSubTotal.add(orderVariationSubTotal);

                orderShop.getOrderVars().add(orderVariation);
            }
            orderShop.setSubTotal(orderShopSubTotal);
            BigDecimal shippingCost = order.getShippingCost() != null
                    ? BigDecimal.valueOf(order.getShippingCost())
                    : BigDecimal.ZERO;
            orderShop.setShippingCost(shippingCost);
            orderShop.setTotalPrice(orderShopSubTotal.add(shippingCost));
            orderGroup.getOrderShops().add(orderShop);
            subTotalAll = subTotalAll.add(orderShopSubTotal);
            totalAmount = totalAmount.add(orderShop.getTotalPrice());
        }
        orderGroup.setTotalPrice(totalAmount);
        orderGroup.setSubTotal(subTotalAll);
        return orderGroup;
    }

    private PlaceOrderResponse saveOrders(PreparedOrderGroup orderGroup, AccountEntity account, AddressEntity address) {
        OrderGroupEntity orderGroupEntity = new OrderGroupEntity();
        orderGroupEntity.setAccountEntity(account);
        orderGroupEntity.setAddressEntity(address);
        orderGroupEntity.setSubTotal(orderGroup.getSubTotal());
        orderGroupEntity.setTotalAmount(orderGroup.getTotalPrice());
        orderGroupEntity.setCode("OG-" + System.currentTimeMillis() + "-" + Integer.toHexString(ThreadLocalRandom.current().nextInt(0, 0x10000)));

        Set<Long> orderedVariationIds = new HashSet<>();

        for (PreparedOrderShop orderShop : orderGroup.getOrderShops()) {
            OrderEntity orderEntity = new OrderEntity();
            orderEntity.setShopEntity(orderShop.getShopEntity());
            orderEntity.setAccountEntity(account);
            orderEntity.setShippingEntity(orderShop.getShippingEntity());
            orderEntity.setOrderGroupEntity(orderGroupEntity);
            orderEntity.setTotalPrice(orderShop.getTotalPrice());
            orderEntity.setShippingCost(orderShop.getShippingCost() != null ? orderShop.getShippingCost() : BigDecimal.ZERO);
            // set status
            for (PreparedOrderVariations orderVariation: orderShop.getOrderVars()) {
                OrderVariationEntity orderVariationEntity = new OrderVariationEntity();
                orderVariationEntity.setOrderEntity(orderEntity);
                orderVariationEntity.setVariationEntity(orderVariation.getVariation());
                orderVariationEntity.setQuantity(orderVariation.getQuantity());
                orderVariationEntity.setPrice(orderVariation.getPriceAtPoint());
                orderEntity.getOrderVariations().add(orderVariationEntity);

                if (orderVariation.getVariation() != null) {
                    orderedVariationIds.add(orderVariation.getVariation().getId());
                }
            }
            orderGroupEntity.getOrders().add(orderEntity);
        }

        orderGroupRepo.save(orderGroupEntity);

        for (OrderEntity order : orderGroupEntity.getOrders()) {
            notificationServices.notifySellerNewOrder(order);
        }

        for (PreparedOrderShop orderShop : orderGroup.getOrderShops()) {
            for (PreparedOrderVariations ov : orderShop.getOrderVars()) {
                inventoryServices.deductStock(ov.getVariation().getId(), ov.getQuantity());
            }
        }

        clearOrderedItemsFromCart(account, orderedVariationIds);

        return new PlaceOrderResponse(
                orderGroupEntity.getId(),
                orderGroupEntity.getCode(),
                orderGroupEntity.getTotalAmount()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShopOrderDto> getOrdersByShop(Long shopId) {
        List<OrderEntity> orders = orderRepo.findByShopIdAndStatusWithDetails(shopId, "PENDING");
        return mapToShopOrderDtos(orders);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShopOrderDto> getOrdersByShopAndStatus(Long shopId, String status) {
        List<OrderEntity> orders = orderRepo.findByShopIdAndStatusWithDetails(shopId, status);
        return mapToShopOrderDtos(orders);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MyOrderDto> getOrdersOfAccount(Long accountId, String status) {
        List<OrderEntity> orders = orderRepo.findByAccountIdWithDetails(accountId);
        String normalizedStatus = (status != null && !status.isBlank()) ? status.trim().toUpperCase() : null;
        return mapToMyOrderDtos(orders, normalizedStatus);
    }

    private List<ShopOrderDto> mapToShopOrderDtos(List<OrderEntity> orders) {
        return orders.stream()
                .map(o -> {
                    ShopOrderDto dto = new ShopOrderDto();
                    dto.setOrderId(o.getId());
                    dto.setOrderGroupCode(o.getOrderGroupEntity() != null ? o.getOrderGroupEntity().getCode() : null);
                    dto.setBuyerEmail(o.getAccountEntity() != null ? o.getAccountEntity().getEmail() : null);
                    dto.setShippingName(o.getShippingEntity() != null ? o.getShippingEntity().getName() : null);
                    dto.setTotalPrice(o.getTotalPrice());
                    dto.setShippingCost(o.getShippingCost() != null ? o.getShippingCost() : BigDecimal.ZERO);
                    dto.setCreatedAt(o.getCreatedAt());
                    dto.setStatus(o.getStatus());
                    dto.setItems(o.getOrderVariations().stream()
                            .map(ov -> {
                                String name = null;
                                String thumbnail = null;
                                String variationName = null;
                                var variation = ov.getVariationEntity();
                                if (variation != null) {
                                    Hibernate.initialize(variation.getVariationOptions());
                                    var opts = variation.getVariationOptions();
                                    if (opts != null && !opts.isEmpty()) {
                                        variationName = opts.stream()
                                                .filter(vo -> vo.getDeletedAt() == null && vo.getOptionsEntity() != null)
                                                .map(vo -> vo.getOptionsEntity().getName())
                                                .collect(Collectors.joining(" / "));
                                        if (variationName != null && variationName.isEmpty()) {
                                            variationName = null;
                                        }
                                    }
                                }
                                if (variation != null && variation.getProductEntity() != null) {
                                    var product = variation.getProductEntity();
                                    name = product.getName();
                                    thumbnail = product.getThumbnail();
                                    if (thumbnail == null || thumbnail.isBlank()) {
                                        Hibernate.initialize(product.getProductImages());
                                        var imgs = product.getProductImages();
                                        if (imgs != null && !imgs.isEmpty()) {
                                            var first = imgs.stream()
                                                    .min(Comparator.comparing(ProductImagesEntity::getSortOrder, Comparator.nullsLast(Comparator.naturalOrder())))
                                                    .orElse(null);
                                            if (first != null && first.getPath() != null) {
                                                thumbnail = first.getPath();
                                            }
                                        }
                                    }
                                }
                                ShopOrderItemDto item = new ShopOrderItemDto();
                                item.setProductName(name);
                                item.setProductThumbnail(thumbnail);
                                item.setVariationName(variationName);
                                item.setQuantity(ov.getQuantity());
                                item.setPrice(ov.getPrice());
                                return item;
                            })
                            .toList());
                    return dto;
                })
                .toList();
    }

    private List<MyOrderDto> mapToMyOrderDtos(List<OrderEntity> orders, String statusFilter) {
        Map<Long, MyOrderDto> groupMap = new HashMap<>();

        for (OrderEntity o : orders) {
            if (o == null) {
                continue;
            }
            if (statusFilter != null) {
                String st = o.getStatus();
                if (st == null || !statusFilter.equalsIgnoreCase(st)) {
                    continue;
                }
            }

            OrderGroupEntity og = o.getOrderGroupEntity();
            if (og == null) {
                continue;
            }

            Long groupId = og.getId();
            if (groupId == null) {
                continue;
            }

            MyOrderDto dto = groupMap.get(groupId);
            if (dto == null) {
                dto = new MyOrderDto();
                dto.setOrderGroupId(groupId);
                dto.setOrderGroupCode(og.getCode());
                dto.setTotalAmount(og.getTotalAmount());
                dto.setSubTotal(og.getSubTotal());
                dto.setCreatedAt(og.getCreatedAt());
                groupMap.put(groupId, dto);
            }

            MyOrderShopDto shopDto = new MyOrderShopDto();
            shopDto.setOrderId(o.getId());
            if (o.getShopEntity() != null) {
                shopDto.setShopId(o.getShopEntity().getId());
                shopDto.setShopName(o.getShopEntity().getName());
            }
            shopDto.setShippingName(o.getShippingEntity() != null ? o.getShippingEntity().getName() : null);
            shopDto.setTotalPrice(o.getTotalPrice());
            shopDto.setShippingCost(o.getShippingCost() != null ? o.getShippingCost() : BigDecimal.ZERO);
            shopDto.setStatus(o.getStatus());

            shopDto.setItems(o.getOrderVariations().stream()
                    .map(ov -> {
                        String name = null;
                        String thumbnail = null;
                        String variationName = null;
                        Long productId = null;
                        Long variationId = null;

                        var variation = ov.getVariationEntity();
                        if (variation != null) {
                            variationId = variation.getId();
                            Hibernate.initialize(variation.getVariationOptions());
                            var opts = variation.getVariationOptions();
                            if (opts != null && !opts.isEmpty()) {
                                variationName = opts.stream()
                                        .filter(vo -> vo.getDeletedAt() == null && vo.getOptionsEntity() != null)
                                        .map(vo -> vo.getOptionsEntity().getName())
                                        .collect(Collectors.joining(" / "));
                                if (variationName != null && variationName.isEmpty()) {
                                    variationName = null;
                                }
                            }
                        }
                        if (variation != null && variation.getProductEntity() != null) {
                            var product = variation.getProductEntity();
                            productId = product.getId();
                            name = product.getName();
                            thumbnail = product.getThumbnail();
                            if (thumbnail == null || thumbnail.isBlank()) {
                                Hibernate.initialize(product.getProductImages());
                                var imgs = product.getProductImages();
                                if (imgs != null && !imgs.isEmpty()) {
                                    var first = imgs.stream()
                                            .min(Comparator.comparing(ProductImagesEntity::getSortOrder, Comparator.nullsLast(Comparator.naturalOrder())))
                                            .orElse(null);
                                    if (first != null && first.getPath() != null) {
                                        thumbnail = first.getPath();
                                    }
                                }
                            }
                        }

                        MyOrderItemDto item = new MyOrderItemDto();
                        item.setProductId(productId);
                        item.setVariationId(variationId);
                        item.setProductName(name);
                        item.setProductThumbnail(thumbnail);
                        item.setVariationName(variationName);
                        item.setQuantity(ov.getQuantity());
                        item.setPrice(ov.getPrice());
                        return item;
                    })
                    .toList());

            dto.getShops().add(shopDto);
        }

        return groupMap.values().stream()
                .filter(g -> g.getShops() != null && !g.getShops().isEmpty())
                // Ensure "latest order" sorting for the buyer's order list.
                .sorted(Comparator.comparing(MyOrderDto::getCreatedAt,
                        Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .toList();
    }

    @Transactional
    protected void markOrderAsShipped(Long sellerAccountId, Long orderId) {
        OrderEntity order = orderRepo.findByIdWithShopOwner(orderId)
                .orElseThrow(() -> new BusinessException("Đơn hàng không tồn tại", HttpStatus.NOT_FOUND));
        Long ownerId = order.getShopEntity() != null && order.getShopEntity().getAccountEntity() != null
                ? order.getShopEntity().getAccountEntity().getId()
                : null;
        if (ownerId == null || !ownerId.equals(sellerAccountId)) {
            throw new BusinessException("Bạn không có quyền cập nhật đơn hàng này", HttpStatus.FORBIDDEN);
        }
        if (!"PENDING".equalsIgnoreCase(order.getStatus())) {
            throw new BusinessException("Chỉ có thể 'Giao hàng' khi đơn đang ở trạng thái PENDING", HttpStatus.BAD_REQUEST);
        }
        order.setStatus("SHIPPED");
        orderRepo.save(order);
        notificationServices.notifyBuyerOrderShipped(order);
    }

    @Transactional
    protected void cancelOrder(Long sellerAccountId, Long orderId) {
        OrderEntity order = orderRepo.findByIdWithShopOwner(orderId)
                .orElseThrow(() -> new BusinessException("Đơn hàng không tồn tại", HttpStatus.NOT_FOUND));
        Long ownerId = order.getShopEntity() != null && order.getShopEntity().getAccountEntity() != null
                ? order.getShopEntity().getAccountEntity().getId()
                : null;
        if (ownerId == null || !ownerId.equals(sellerAccountId)) {
            throw new BusinessException("Bạn không có quyền cập nhật đơn hàng này", HttpStatus.FORBIDDEN);
        }
        if (!"PENDING".equalsIgnoreCase(order.getStatus())) {
            throw new BusinessException("Chỉ có thể hủy đơn khi trạng thái là PENDING", HttpStatus.BAD_REQUEST);
        }
        order.setStatus("CANCELED");
        orderRepo.save(order);
        notificationServices.notifyBuyerOrderCancelledBySeller(order);
    }

    @Override
    @Transactional
    public void markOrdersAsShipped(Long sellerAccountId, List<Long> orderIds) {
        if (orderIds == null || orderIds.isEmpty()) {
            return;
        }
        for (Long id : orderIds) {
            markOrderAsShipped(sellerAccountId, id);
        }
    }

    @Override
    @Transactional
    public void cancelOrders(Long sellerAccountId, List<Long> orderIds) {
        if (orderIds == null || orderIds.isEmpty()) {
            return;
        }
        for (Long id : orderIds) {
            cancelOrder(sellerAccountId, id);
        }
    }

    private void clearOrderedItemsFromCart(AccountEntity account, Set<Long> orderedVariationIds) {
        if (orderedVariationIds == null || orderedVariationIds.isEmpty()) {
            return;
        }

        cartRepo.findByAccountEntityId(account.getId())
                .ifPresent(cart -> {
                    boolean changed = false;
                    for (CartVariationEntity cv : cart.getCartVariations()) {
                        if (cv.getDeletedAt() == null
                                && cv.getVariationEntity() != null
                                && orderedVariationIds.contains(cv.getVariationEntity().getId())) {
                            cv.softDelete();
                            changed = true;
                        }
                    }
                    if (changed) {
                        cartRepo.save(cart);
                    }
                });
    }
}