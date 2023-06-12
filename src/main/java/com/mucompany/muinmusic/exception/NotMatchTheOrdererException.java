package com.mucompany.muinmusic.exception;

public class NotMatchTheOrdererException extends RuntimeException {
    public NotMatchTheOrdererException() {
        super("주문자와 로그인한 회원이 일치하지 않습니다.");
    }
}
