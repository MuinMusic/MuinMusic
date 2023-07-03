package com.mucompany.muinmusic.order.app;

import com.mucompany.muinmusic.cart.domain.CartItem;
import com.mucompany.muinmusic.member.domain.Member;
import com.mucompany.muinmusic.order.domain.OrderItem;

import java.util.List;

public interface OrderService {

    OrderResponse placeOrder(OrderRequest orderRequest);
    void itemStockDecrease(CartItem cartItem, Long itemId);

    void itemStockIncrease(CartItem cartItem, Long itemId);

    OrderResponse save(List<CartItem> cartItemList, Member member, OrderRequest orderRequest);

    void cancel(Long orderId, Long memberId);

}
