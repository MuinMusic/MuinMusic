package com.mucompany.muinmusic.exception;

public class ItemNameNotMatchException extends RuntimeException{
    public ItemNameNotMatchException() {
        super("기존 상품의 이름이 변경되었습니다.");
    }
}
