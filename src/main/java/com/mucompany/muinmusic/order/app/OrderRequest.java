package com.mucompany.muinmusic.order.app;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class OrderRequest {

    private Long memberId;

    private List<Long> orderItemIdList;

    private String address;

    private LocalDateTime orderDate;

    @Builder
    public OrderRequest(Long memberId, List<Long> orderItemIdList, String address, LocalDateTime orderDate) {
        this.memberId = memberId;
        this.orderItemIdList = orderItemIdList;
        this.address = address;
        this.orderDate = orderDate;
    }
}
