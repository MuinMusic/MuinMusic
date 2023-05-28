package com.mucompany.muinmusic.article.app;

import com.mucompany.muinmusic.article.Article;
import com.mucompany.muinmusic.article.api.dto.ArticleCreateDto;
import com.mucompany.muinmusic.article.repository.ArticleRepository;
import com.mucompany.muinmusic.domain.Genre;
import com.mucompany.muinmusic.domain.Part;
import com.mucompany.muinmusic.domain.Skill;
import com.mucompany.muinmusic.exception.ArticleNotFoundException;
import com.mucompany.muinmusic.exception.ArticleValidityCheckException;
import com.mucompany.muinmusic.exception.UserNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
public class ArticleServiceTest {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private ArticleRepository articleRepository;

    @DisplayName(value = "articleCreateDto,userId 유효하면 글 저장 성공")
    @Test
    void t1() throws UserNotFoundException, ArticleValidityCheckException {
        ArticleCreateDto articleCreateDto = ArticleCreateDto.builder()
                .title("10년차 드러머 레슨생 모집합니다")
                .career("가수 누구누구 서울투어 콘서트 등등")
                .price(100000)
                .part(Part.STUDIO_SESSION)
                .skill(Skill.DRUM)
                .genre(Genre.POP)
                .build();

        Long userId = 1L;

        Article article = articleService.createArticle(articleCreateDto, userId);

        assertThat(article.getTitle()).isEqualTo(articleCreateDto.getTitle());
        assertThat(article.getUserId()).isEqualTo(userId);
    }

    @DisplayName(value = "articleId 유효하면 찾아서 삭제 성공")
    @Test
    void t2() throws ArticleNotFoundException {

        creatArticle();

        Long articleId = 1L;

        articleService.delete(articleId);

        assertThat(articleRepository.findById(articleId)).isEmpty();
    }

    @DisplayName(value = "articleId 유효하지못하면 예외 발생")
    @Test
    void t3() {

        creatArticle();

        Long articleId = 10L;

        assertThatThrownBy(() -> {

            articleService.delete(articleId);

        }).isInstanceOf(ArticleNotFoundException.class);

    }

    private void creatArticle() {
        Article article = Article.builder()
                .title("안녕하세요")
                .userId(1L)
                .career("누구누구 세션 , 교회 반주 10년")
                .price(30000)
                .part(Part.LIVE_SESSION)
                .skill(Skill.GUITAR)
                .genre(Genre.POP)
                .build();

        articleRepository.save(article);
    }
}