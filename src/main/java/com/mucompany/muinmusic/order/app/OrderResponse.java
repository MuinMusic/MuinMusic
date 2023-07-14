package com.mucompany.muinmusic.order.app;

import com.mucompany.muinmusic.cart.domain.CartItem;
import com.mucompany.muinmusic.order.domain.OrderStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class OrderResponse {

    private Long memberId;

    private List<Long> orderItemIdList;

    private String address;

    private OrderStatus orderStatus;

    private LocalDateTime orderDate;

    @Builder
    public OrderResponse(Long memberId, List<Long> orderItemIdList, String address, OrderStatus orderStatus, LocalDateTime orderDate) {
        this.memberId = memberId;
        this.orderItemIdList = orderItemIdList;
        this.address = address;
        this.orderStatus = orderStatus;
        this.orderDate = orderDate;
    }
}
