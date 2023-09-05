package com.mucompany.muinmusic.order.api;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.mucompany.muinmusic.order.app.OrderResponse;
import com.mucompany.muinmusic.order.domain.OrderStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@JsonRootName(value = "order")
public class OrderResponseDto {

    private Long memberId;

    private List<Long> orderItemIdList;

    private OrderStatus orderStatus;

    private String address;

    private LocalDateTime orderDate;

    @Builder
    public OrderResponseDto(OrderResponse orderResponse) {
        this.memberId = orderResponse.memberId();
        this.orderItemIdList = orderResponse.orderItemIdList();
        this.orderStatus = orderResponse.orderStatus();
        this.address = orderResponse.address();
        this.orderDate = orderResponse.orderDate();
    }
}
