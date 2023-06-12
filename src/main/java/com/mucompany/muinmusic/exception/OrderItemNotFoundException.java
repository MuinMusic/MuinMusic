package com.mucompany.muinmusic.exception;

public class OrderItemNotFoundException extends RuntimeException {
    public OrderItemNotFoundException() {
        super("주문 상품을 찾을 수 없습니다");
    }
}
