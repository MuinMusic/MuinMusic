package com.mucompany.muinmusic.article.app;

import com.mucompany.muinmusic.article.Article;
import com.mucompany.muinmusic.article.api.dto.ArticleCreateDto;
import com.mucompany.muinmusic.article.repository.ArticleRepository;
import com.mucompany.muinmusic.exception.ArticleNotFoundException;
import com.mucompany.muinmusic.exception.ArticleValidityCheckException;
import com.mucompany.muinmusic.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;

    @Override
    public Article createArticle(ArticleCreateDto articleCreateDto, Long userId) throws UserNotFoundException, ArticleValidityCheckException {
        if (userId == null) {
            throw new UserNotFoundException("유저를 찾을 수 없습니다.");
        }

        Article article = getArticle(articleCreateDto, userId);

        if (article.getTitle().equals("")) {
            throw new ArticleValidityCheckException("제목을 입력해주세요.");
        }

        return articleRepository.save(article);
    }

    @Override
    public void delete(Long articleId) throws ArticleNotFoundException {
        Article article = articleRepository.findById(articleId).orElseThrow(ArticleNotFoundException::new);

        articleRepository.delete(article);
    }

    private static Article getArticle(ArticleCreateDto articleCreateDto, Long userId) {
        Article article = Article.builder()
                .userId(userId)
                .title(articleCreateDto.getTitle())
                .career(articleCreateDto.getCareer())
                .price(articleCreateDto.getPrice())
                .part(articleCreateDto.getPart())
                .skill(articleCreateDto.getSkill())
                .genre(articleCreateDto.getGenre())
                .build();
        return article;
    }
}