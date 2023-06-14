package com.mucompany.muinmusic.promotion.core;

import com.mucompany.muinmusic.exception.MemberNotFoundException;
import com.mucompany.muinmusic.promotion.entry.domain.AlreadyEnteredPromotionException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class PromotionGlobalExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handle(PromotionNotFoundException e) {
        // TODO logging, 응답 정의
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handle(PromotionNotActiveException e) {
        // TODO logging, 응답 정의
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handle(MemberNotFoundException e) {
        // TODO logging, 응답 정의
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public void handle(AlreadyEnteredPromotionException e) {
        // TODO logging, 응답 정의
    }

}
