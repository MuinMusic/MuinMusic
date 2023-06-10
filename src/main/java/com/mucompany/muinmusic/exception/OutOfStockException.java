package com.mucompany.muinmusic.exception;

public class OutOfStockException extends RuntimeException{
    public OutOfStockException() {
        super("상품의 재고가 부족합니다");
    }
}
