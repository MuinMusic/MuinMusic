package com.mucompany.muinmusic.order.app;

import java.util.List;

public interface OrderService {

    OrderResponse placeOrder(OrderRequest orderRequest);

    OrderResponse placeOrderWithoutLock(OrderRequest orderRequest);

    void cancel(Long orderId, Long memberId);

    void decrease(List<Long> orderItemIdList);

    void decreaseWithoutLock(List<Long> orderItemIdList);
}
