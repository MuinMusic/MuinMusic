package com.mucompany.muinmusic.exception;

public class OrderCancellationException extends RuntimeException {
    public OrderCancellationException() {
        super("배송중인 상품은 취소할 수 없습니다.");
    }
}
