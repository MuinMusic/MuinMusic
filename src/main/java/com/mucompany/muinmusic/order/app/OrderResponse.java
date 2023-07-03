package com.mucompany.muinmusic.order.app;

import com.mucompany.muinmusic.cart.domain.CartItem;
import com.mucompany.muinmusic.order.domain.OrderStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class OrderResponse {

    private Long memberId;

    private List<Long> orderItemIdList;

    private String address;

    private OrderStatus orderStatus;

    private LocalDateTime orderDate;

    @Builder
    public OrderResponse(OrderRequest orderRequest, List<Long> orderItemIdList, OrderStatus orderStatus) {
        this.memberId = orderRequest.getMemberId();
        this.orderItemIdList = orderItemIdList;
        this.address = orderRequest.getAddress();
        this.orderStatus = orderStatus;
        this.orderDate = orderRequest.getOrderDate();

    }


}
