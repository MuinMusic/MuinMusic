package com.mucompany.muinmusic.order.app;

import com.mucompany.muinmusic.order.domain.OrderStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class OrderResponse {

    private Long memberId;

    private List<Long> orderItemIdList;

    private String address;

    private OrderStatus orderStatus;

    private LocalDateTime orderDate;

    @Builder
    public OrderResponse(OrderRequest orderRequest, OrderStatus orderStatus) {
        this.memberId = orderRequest.getMemberId();
        this.orderItemIdList = orderRequest.getOrderItemIdList();
        this.address = orderRequest.getAddress();
        this.orderStatus = orderStatus;
        this.orderDate = orderRequest.getOrderDate();

    }
}
