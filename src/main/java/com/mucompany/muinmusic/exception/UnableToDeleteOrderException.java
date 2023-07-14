package com.mucompany.muinmusic.exception;

public class UnableToDeleteOrderException extends RuntimeException {
    public UnableToDeleteOrderException() {
        super("주문내역을 삭제할 수 없습니다");
    }
}
