package com.mucompany.muinmusic.order.api;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Builder;

import java.time.LocalDateTime;

@JsonTypeName(value = "order")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
public record OrderRequestDto(Long memberId, Long cartId, String address, LocalDateTime orderDate) {

    @Builder
    public OrderRequestDto {
    }
}
