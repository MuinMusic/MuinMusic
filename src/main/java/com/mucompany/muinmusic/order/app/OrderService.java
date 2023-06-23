package com.mucompany.muinmusic.order.app;

import java.util.List;

public interface OrderService {

    OrderResponse placeOrderWithRedissonLock(OrderRequest orderRequest);

    OrderResponse placeOrderWithoutRedissonLock(OrderRequest orderRequest);

    OrderResponse placeOrderWithoutPessimisticLock(OrderRequest orderRequest);

    OrderResponse placeOrderWithPessimisticLock(OrderRequest orderRequest);

    void cancel(Long orderId, Long memberId);

    void decrease(List<Long> orderItemIdList);

    void decreaseWithoutLock(List<Long> orderItemIdList);
}
