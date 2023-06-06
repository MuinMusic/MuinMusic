package com.mucompany.muinmusic.order.api;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.mucompany.muinmusic.order.app.OrderRequest;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@JsonRootName(value = "order")
public class OrderResponseDto {

    private Long memberId;

    private List<Long> orderItemIdList;

    private String orderStatus;

    private String address;

    private LocalDateTime orderDate;

    @Builder
    public OrderResponseDto(OrderRequest orderRequest) {
        this.memberId = orderRequest.getMemberId();
        this.orderItemIdList = orderRequest.getOrderItemIdList();
        this.orderStatus = orderRequest.getOrderStatus();
        this.address = orderRequest.getAddress();
        this.orderDate = orderRequest.getOrderDate();
    }
}
