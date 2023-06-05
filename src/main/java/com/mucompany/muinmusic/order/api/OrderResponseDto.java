package com.mucompany.muinmusic.order.api;

import com.mucompany.muinmusic.order.app.ConvertOrderDto;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class OrderResponseDto {

    private Long memberId;

    private List<Long> orderItemIdList;

    private String orderStatus;

    private String address;

    private LocalDateTime orderDate;

    @Builder
    public OrderResponseDto(ConvertOrderDto convertOrderDto) {
        this.memberId = convertOrderDto.getMemberId();
        this.orderItemIdList = convertOrderDto.getOrderItemIdList();
        this.orderStatus = convertOrderDto.getOrderStatus();
        this.address = convertOrderDto.getAddress();
        this.orderDate = convertOrderDto.getOrderDate();
    }
}
