package com.mucompany.muinmusic.exception;

public class OrderFailException extends RuntimeException {
    public OrderFailException() {
        super("주문을 처리할 수 없습니다.");
    }
}
