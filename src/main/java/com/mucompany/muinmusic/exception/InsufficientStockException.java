package com.mucompany.muinmusic.exception;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException() {
        super("상품의 재고가 부족합니다");
    }
}
