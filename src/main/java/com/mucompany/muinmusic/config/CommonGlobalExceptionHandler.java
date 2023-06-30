package com.mucompany.muinmusic.config;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class CommonGlobalExceptionHandler {
//    @ExceptionHandler({IllegalArgumentException.class, MethodArgumentTypeMismatchException.class, ConstraintViolationException.class})
//    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
//    public void handle(Exception e) {
//        // TODO logging, 응답 정의
//    }

}
