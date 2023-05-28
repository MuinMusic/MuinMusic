package com.mucompany.muinmusic.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)

public class ArticleNotFoundException extends Exception{
    public ArticleNotFoundException() {
        super("게시글이 존재하지 않습니다");
    }
}
