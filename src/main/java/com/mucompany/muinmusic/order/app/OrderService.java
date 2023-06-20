package com.mucompany.muinmusic.order.app;

import com.mucompany.muinmusic.order.domain.OrderItem;

import java.util.List;

public interface OrderService {

    OrderResponse placeOrder(OrderRequest orderRequest);

    OrderResponse placeOrder2(OrderRequest orderRequest);

    void cancel(Long orderId, Long memberId);

    void decrease(List<Long> orderItemIdList);

    void decrease2(List<Long> orderItemIdList);
}
