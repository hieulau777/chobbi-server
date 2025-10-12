package com.chobbi.server.services;

import com.chobbi.server.payload.request.OrderRequest;

public interface OrdersServices {
    void createOrders(OrderRequest request);
}
