package com.mucompany.muinmusic.article.api;

import com.mucompany.muinmusic.article.Article;
import com.mucompany.muinmusic.article.api.dto.ArticleCreateDto;
import com.mucompany.muinmusic.article.app.ArticleService;
import com.mucompany.muinmusic.exception.ArticleValidityCheckException;
import com.mucompany.muinmusic.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    @PostMapping(value = "/article")
    public Article create(@RequestBody ArticleCreateDto articleCreateDto) throws UserNotFoundException, ArticleValidityCheckException {
        return articleService.createArticle(articleCreateDto, getCurrentUserId());
    }

    private Long getCurrentUserId() {
        return 1L;
    }
}