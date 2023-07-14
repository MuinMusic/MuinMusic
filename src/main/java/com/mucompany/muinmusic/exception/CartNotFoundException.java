package com.mucompany.muinmusic.exception;

public class CartNotFoundException extends RuntimeException {
    public CartNotFoundException() {
        super("장바구니를 불러 올 수 없습니다");
    }
}
