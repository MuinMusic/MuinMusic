package com.mucompany.muinmusic.order.api;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@JsonTypeName(value = "order")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
public class OrderRequestDto {

    private Long memberId;

    private Long cartId;

    private String address;

    private LocalDateTime orderDate;

    @Builder
    public OrderRequestDto(Long memberId, Long cartId, String address, LocalDateTime orderDate) {
        this.memberId = memberId;
        this.cartId = cartId;
        this.address = address;
        this.orderDate = orderDate;
    }
}
