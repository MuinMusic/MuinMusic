package com.mucompany.muinmusic.article.app;

import com.mucompany.muinmusic.article.Article;
import com.mucompany.muinmusic.article.api.dto.ArticleCreateDto;
import com.mucompany.muinmusic.exception.ArticleValidityCheckException;
import com.mucompany.muinmusic.exception.UserNotFoundException;

public interface ArticleService {

    Article createArticle(ArticleCreateDto articleCreateDto, Long userId) throws UserNotFoundException, ArticleValidityCheckException;
}
