package com.mucompany.muinmusic.order.api;

import com.mucompany.muinmusic.order.domain.Order;
import com.mucompany.muinmusic.order.domain.OrderItem;
import com.mucompany.muinmusic.order.domain.OrderStatus;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class OrderDto {


    private MemberDto member;

    private List<OrderItemDto> orderItems;

    private OrderStatus orderStatus;

    private String address;

    private LocalDateTime orderDate;

    public OrderDto(Order order) {
        this.member = new MemberDto(order);
        this.orderItems = order.getOrderItems().stream().map(OrderItemDto::new).toList();
        this.orderStatus = order.getOrderStatus();
        this.address = order.getAddress();
        this.orderDate = order.getOrderDate();
    }

    @Getter
    static class MemberDto {

        private String name;

        public MemberDto(Order order) {
            this.name = order.getMember().getName();
        }
    }

    @Getter
    static class OrderItemDto {

        private Long itemId;

        private int count;

        private int totalAmount;

        public OrderItemDto(OrderItem orderItem) {
            this.itemId = orderItem.getItemId();
            this.count = orderItem.getCount();
            this.totalAmount = orderItem.getTotalAmount();
        }
    }
}
