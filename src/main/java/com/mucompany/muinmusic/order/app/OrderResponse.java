package com.mucompany.muinmusic.order.app;

import com.mucompany.muinmusic.order.domain.OrderStatus;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;


public record OrderResponse(Long memberId, List<Long> orderItemIdList, String address, OrderStatus orderStatus, LocalDateTime orderDate) {

    @Builder
    public OrderResponse {
    }
}
