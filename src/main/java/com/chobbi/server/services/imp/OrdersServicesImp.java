package com.chobbi.server.services.imp;

import com.chobbi.server.dto.OrderProductRequestDto;
import com.chobbi.server.entity.AccountEntity;
import com.chobbi.server.entity.OrdersEntity;
import com.chobbi.server.entity.ShopEntity;
import com.chobbi.server.payload.request.OrderRequest;
import com.chobbi.server.repo.*;
import com.chobbi.server.services.OrdersServices;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrdersServicesImp implements OrdersServices {
    private final AccountRepo accountRepo;
    private final ProductRepo productRepo;
    private final VariationRepo variationRepo;
    private final ShopRepo shopRepo;
    private final OrdersRepo ordersRepo;
    private final OrderVariationRepo orderVariationRepo;

    @Override
    @Transactional
    public void createOrders(OrderRequest request) {
        AccountEntity account = accountRepo.findById(request.getAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found"));
        Map<Long, List<OrderProductRequestDto>> productsByShop =
                request.getOrders().stream()
                        .collect(Collectors.groupingBy(OrderProductRequestDto::getShopId));
        for (Map.Entry<Long, List<OrderProductRequestDto>> entry : productsByShop.entrySet()) {
            ShopEntity shop = shopRepo.findById(entry.getKey())
                    .orElseThrow(() -> new RuntimeException("Shop not found"));
            Long shopId = entry.getKey();
            List<OrderProductRequestDto> shopProducts = entry.getValue();

            OrdersEntity order = new OrdersEntity();
            order.setAccountEntity(account);
            order.setShopId(shopId);
            order.setStatus("PENDING");
            order.setCreatedAt(LocalDateTime.now());

            ordersRepository.save(order);

            for (OrderProductRequestDto p : shopProducts) {
                OrderVariation orderVariation = new OrderVariation();
                orderVariation.setOrder(order);
                orderVariation.setProductId(p.getProductId());
                orderVariation.setVariationId(p.getVariationId());
                orderVariation.setQuantity(p.getQuantity());
                orderVariationRepository.save(orderVariation);
            }
        }
    }
}
