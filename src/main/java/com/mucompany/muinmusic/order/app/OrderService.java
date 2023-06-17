package com.mucompany.muinmusic.order.app;

public interface OrderService {

    OrderResponse placeOrder(OrderRequest orderRequest);

    void cancel(Long orderId, Long memberId);
}
