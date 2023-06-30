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

    private List<Long> orderItemIdList;

    private String address;

    private LocalDateTime orderDate;

    @Builder
    public OrderRequestDto(Long memberId, List<Long> orderItemIdList, String address, LocalDateTime orderDate) {
        this.memberId = memberId;
        this.orderItemIdList = orderItemIdList;
        this.address = address;
        this.orderDate = orderDate;
    }
}
