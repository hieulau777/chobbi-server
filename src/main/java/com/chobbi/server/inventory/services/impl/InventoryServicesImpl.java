package com.chobbi.server.inventory.services.impl;

import com.chobbi.server.catalog.entity.VariationEntity;
import com.chobbi.server.catalog.repo.VariationRepo;
import com.chobbi.server.exception.BusinessException;
import com.chobbi.server.inventory.services.InventoryServices;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryServicesImpl implements InventoryServices {
    private final VariationRepo variationRepo;

    @Override
    public boolean isStockAvailable(Integer currentStock, Integer reqQuantity) {
        return currentStock != null && reqQuantity != null && currentStock >= reqQuantity;
    }

    @Override
    public int getAvailableQuantity(Long variationId) {
        VariationEntity variation = variationRepo.findByIdAndDeletedAtIsNull(variationId)
                .orElseThrow(() -> new BusinessException("Variation not found: " + variationId, HttpStatus.BAD_REQUEST));
        return variation.getStock() != null ? variation.getStock() : 0;
    }

    @Override
    public void validateQuantityAvailable(Long variationId, Integer requestedQuantity) {
        if (requestedQuantity == null || requestedQuantity <= 0) {
            throw new BusinessException("Số lượng không hợp lệ", HttpStatus.BAD_REQUEST);
        }
        int available = getAvailableQuantity(variationId);
        if (requestedQuantity > available) {
            throw new BusinessException("Không đủ số lượng. Còn lại: " + available, HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    @Transactional
    public void deductStock(Long variationId, Integer quantity) {
        int updatedRows = variationRepo.decreaseStock(variationId, quantity);
        if (updatedRows == 0) {
            throw new BusinessException("Rất tiếc, sản phẩm đã hết hàng hoặc không đủ số lượng!", HttpStatus.BAD_REQUEST);
        }
    }
}
