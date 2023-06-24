package com.mucompany.muinmusic.order.app;

import com.mucompany.muinmusic.order.domain.OrderItem;

public interface OrderService {

    OrderResponse placeOrder(OrderRequest orderRequest, OrderItem orderItem, Long key);

    OrderResponse placeOrder(OrderRequest orderRequest);

    void cancel(Long orderId, Long memberId);
}
