package com.chobbi.server.inventory.services;

public interface InventoryServices {
    boolean isStockAvailable(Integer currentStock, Integer quantity);

    /**
     * Available quantity for a variation (variation.stock).
     */
    int getAvailableQuantity(Long variationId);

    /**
     * Throws if requested quantity exceeds getAvailableQuantity(variationId).
     */
    void validateQuantityAvailable(Long variationId, Integer requestedQuantity);

    /**
     * Deduct variation stock.
     */
    void deductStock(Long variationId, Integer quantity);
}
