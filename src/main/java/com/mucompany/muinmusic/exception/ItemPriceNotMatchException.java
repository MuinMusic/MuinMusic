package com.mucompany.muinmusic.exception;

public class ItemPriceNotMatchException extends RuntimeException {
    public ItemPriceNotMatchException() {
        super("기존 상품의 가격이 변경되었습니다");
    }
}
