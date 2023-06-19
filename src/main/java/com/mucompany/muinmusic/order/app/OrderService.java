package com.mucompany.muinmusic.order.app;

import com.mucompany.muinmusic.order.domain.OrderItem;

import java.util.List;

public interface OrderService {

    OrderResponse placeOrder(OrderRequest orderRequest);

    void cancel(Long orderId, Long memberId);

    void validate(List<Long> orderItemIdList, List<OrderItem> orderItemList);
}
