package com.mucompany.muinmusic.exception;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException() {
        super("주문한 상품을 찾을 수 없습니다.");
    }
}
