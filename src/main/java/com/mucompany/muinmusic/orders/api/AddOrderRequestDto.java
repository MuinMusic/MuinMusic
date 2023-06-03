package com.mucompany.muinmusic.orders.api;

import com.mucompany.muinmusic.member.domain.Member;
import com.mucompany.muinmusic.orderItem.domain.OrderItem;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class AddOrderRequestDto {

    private Member member;

    private List<OrderItem> orderItems;

    private String orderStatus;

    private String address;

    private LocalDateTime orderDate;
    @Builder
    public AddOrderRequestDto(Member member, List<OrderItem> orderItems, String orderStatus, String address, LocalDateTime orderDate) {
        this.member = member;
        this.orderItems = orderItems;
        this.orderStatus = orderStatus;
        this.address = address;
        this.orderDate = orderDate;
    }
}
