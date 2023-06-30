package com.mucompany.muinmusic.order.app;

import com.mucompany.muinmusic.member.domain.Member;
import com.mucompany.muinmusic.order.domain.OrderItem;

import java.util.List;

public interface OrderService {

    void itemStockDecrease(OrderItem orderItem, Long itemId);

    void itemStockIncrease(OrderItem orderItem, Long itemId);

    OrderResponse save(List<OrderItem> orderItemList, Member member, OrderRequest orderRequest);

    void cancel(Long orderId, Long memberId);

}
