package com.mucompany.muinmusic.order.api;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.mucompany.muinmusic.order.domain.OrderStatus;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@JsonRootName(value = "order")
public record OrderResponseDto(Long memberId, List<Long> orderItemIdList, OrderStatus orderStatus, String address, LocalDateTime orderDate) {

    @Builder
    public OrderResponseDto {
    }
}
