package com.mucompany.muinmusic.orders.api;

import com.mucompany.muinmusic.member.domain.Member;
import com.mucompany.muinmusic.orderItem.domain.OrderItem;
import com.mucompany.muinmusic.orders.domain.Orders;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
@Getter
public class OrderResponseDto {

    private Member member;

    private List<OrderItem> orderItems;

    private String orderStatus;

    private String address;

    private LocalDateTime orderDate;

    public OrderResponseDto(Orders orders) {
        this.member = orders.getMember();
        this.orderItems = orders.getOrderItems();
        this.orderStatus = orders.getOrderStatus();
        this.address = orders.getAddress();
        this.orderDate = orders.getOrderDate();
    }
}
