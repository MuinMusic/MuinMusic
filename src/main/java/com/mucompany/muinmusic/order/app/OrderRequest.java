package com.mucompany.muinmusic.order.app;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class OrderRequest {

    private Long memberId;

    private Long cartId;

    private String address;

    private LocalDateTime orderDate;

    @Builder
    public OrderRequest(Long memberId, Long cartId, String address, LocalDateTime orderDate) {
        this.memberId = memberId;
        this.cartId = cartId;
        this.address = address;
        this.orderDate = orderDate;
    }
}
