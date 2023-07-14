package com.mucompany.muinmusic.exception;

public class CartItemNotFoundException extends RuntimeException {
    public CartItemNotFoundException() {
        super("주문 상품을 찾을 수 없습니다");
    }
}
