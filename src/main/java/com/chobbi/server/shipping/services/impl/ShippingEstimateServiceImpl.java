package com.chobbi.server.shipping.services.impl;

import com.chobbi.server.catalog.entity.ProductEntity;
import com.chobbi.server.catalog.repo.ProductRepo;
import com.chobbi.server.exception.BusinessException;
import com.chobbi.server.shipping.dto.ShopProductIdsRequest;
import com.chobbi.server.shipping.dto.ShopShippingOptionsDto;
import com.chobbi.server.shipping.dto.ShippingOptionDto;
import com.chobbi.server.shipping.entity.ShippingEntity;
import com.chobbi.server.shipping.repo.ShippingRepo;
import com.chobbi.server.shipping.services.ShippingEstimateService;
import com.chobbi.server.shop.repo.ShopRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShippingEstimateServiceImpl implements ShippingEstimateService {

    private final ShopRepo shopRepo;
    private final ProductRepo productRepo;
    private final ShippingRepo shippingRepo;

    @Override
    @Transactional(readOnly = true)
    public List<ShopShippingOptionsDto> estimateByShopProducts(List<ShopProductIdsRequest> request) {
        if (request == null || request.isEmpty()) {
            throw new BusinessException("Danh sách shop và sản phẩm không được rỗng", HttpStatus.BAD_REQUEST);
        }

        List<ShopShippingOptionsDto> result = new ArrayList<>();
        List<ShippingEntity> shippingMethods = shippingRepo.findAllByDeletedAtIsNull();

        if (shippingMethods.isEmpty()) {
            for (ShopProductIdsRequest item : request) {
                result.add(new ShopShippingOptionsDto(item.getShopId(), List.of()));
            }
            return result;
        }

        for (ShopProductIdsRequest item : request) {
            Long shopId = item.getShopId();
            List<Long> productIds = item.getProductIds() != null ? item.getProductIds() : List.of();

            if (shopId == null) {
                throw new BusinessException("Shop id không được null", HttpStatus.BAD_REQUEST);
            }

            if (!shopRepo.findByIdAndDeletedAtIsNull(shopId).isPresent()) {
                throw new BusinessException("Không tìm thấy shop với id: " + shopId, HttpStatus.NOT_FOUND);
            }

            long totalWeightGram = 0L;
            if (!productIds.isEmpty()) {
                Set<Long> distinctIds = productIds.stream().filter(id -> id != null).collect(Collectors.toSet());
                if (distinctIds.isEmpty()) {
                    result.add(buildShopOptions(shopId, 0L, shippingMethods));
                    continue;
                }
                List<ProductEntity> products = productRepo.findByIdInAndShopEntity_IdAndDeletedAtIsNull(distinctIds, shopId);
                if (products.size() != distinctIds.size()) {
                    throw new BusinessException(
                            "Một hoặc nhiều sản phẩm không tồn tại hoặc không thuộc shop id " + shopId,
                            HttpStatus.BAD_REQUEST
                    );
                }
                for (ProductEntity p : products) {
                    totalWeightGram += (p.getWeight() != null ? p.getWeight() : 0L);
                }
            }

            result.add(buildShopOptions(shopId, totalWeightGram, shippingMethods));
        }

        return result;
    }

    private ShopShippingOptionsDto buildShopOptions(Long shopId, long totalWeightGram, List<ShippingEntity> methods) {
        List<ShippingOptionDto> options = methods.stream()
                .map(m -> {
                    long bw = m.getBaseWeight() != null ? m.getBaseWeight().longValue() : 0L;
                    long bf = m.getBaseFee() != null ? m.getBaseFee().longValue() : 0L;
                    long ws = m.getWeightStep() != null ? m.getWeightStep().longValue() : 0L;
                    long ef = m.getExtraFeePerStep() != null ? m.getExtraFeePerStep().longValue() : 0L;
                    long cost = calcShippingFee(totalWeightGram, bw, bf, ws, ef);
                    ShippingOptionDto dto = new ShippingOptionDto();
                    dto.setId(m.getId());
                    dto.setMethodName(m.getName());
                    dto.setCost(cost);
                    dto.setBaseWeight(bw);
                    dto.setBaseFee(bf);
                    dto.setWeightStep(ws);
                    dto.setExtraFeePerStep(ef);
                    return dto;
                })
                .toList();
        return new ShopShippingOptionsDto(shopId, options);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShippingOptionDto> estimateByWeight(long weightGram) {
        List<ShippingEntity> methods = shippingRepo.findAllByDeletedAtIsNull();
        return buildOptionsFromWeight(weightGram, methods);
    }

    private List<ShippingOptionDto> buildOptionsFromWeight(long totalWeightGram, List<ShippingEntity> methods) {
        return methods.stream()
                .map(m -> {
                    long bw = m.getBaseWeight() != null ? m.getBaseWeight().longValue() : 0L;
                    long bf = m.getBaseFee() != null ? m.getBaseFee().longValue() : 0L;
                    long ws = m.getWeightStep() != null ? m.getWeightStep().longValue() : 0L;
                    long ef = m.getExtraFeePerStep() != null ? m.getExtraFeePerStep().longValue() : 0L;
                    long cost = calcShippingFee(totalWeightGram, bw, bf, ws, ef);
                    ShippingOptionDto dto = new ShippingOptionDto();
                    dto.setId(m.getId());
                    dto.setMethodName(m.getName());
                    dto.setCost(cost);
                    return dto;
                })
                .toList();
    }

    private static long calcShippingFee(long weightGram, long baseWeight, long baseFee, long weightStep, long extraFeePerStep) {
        if (weightStep <= 0) return baseFee;
        if (weightGram <= 0 || weightGram <= baseWeight) return baseFee;
        long steps = (long) Math.ceil((double) (weightGram - baseWeight) / weightStep);
        return baseFee + steps * extraFeePerStep;
    }
}
