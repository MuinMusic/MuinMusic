package com.mucompany.muinmusic.order.app;

import lombok.Builder;

import java.time.LocalDateTime;

public record OrderRequest (Long memberId, Long cartId,String address, LocalDateTime orderDate){

    @Builder
    public OrderRequest {
    }
}
