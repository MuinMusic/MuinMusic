package com.mucompany.muinmusic.article.repository;

import com.mucompany.muinmusic.article.Article;
import com.mucompany.muinmusic.domain.Genre;
import com.mucompany.muinmusic.domain.Part;
import com.mucompany.muinmusic.domain.Skill;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ArticleRepositoryTest {

    @Autowired
    private ArticleRepository articleRepository;

    @DisplayName(value = "article 유효하면 저장 성공")
    @Test
    void t1() {
        Article article = Article.builder()
                .title("안녕하세요")
                .userId(1L)
                .career("누구누구 세션 , 교회 반주 10년")
                .price(30000)
                .part(Part.LIVE_SESSION)
                .skill(Skill.GUITAR)
                .genre(Genre.POP)
                .build();

        Article savedArticle = articleRepository.save(article);

        assertThat(savedArticle.getPart()).isEqualTo(article.getPart());
    }
}
