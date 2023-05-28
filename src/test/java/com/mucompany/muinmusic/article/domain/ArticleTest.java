package com.mucompany.muinmusic.article.domain;

import com.mucompany.muinmusic.article.Article;
import com.mucompany.muinmusic.domain.Genre;
import com.mucompany.muinmusic.domain.Part;
import com.mucompany.muinmusic.domain.Skill;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ArticleTest {

    @DisplayName(value = "필수값 유효하면 빌드 성공")
    @Test
    void t1() {

        Article.builder()
                .title("안녕하세요")
                .userId(1L)
                .career("누구누구 세션 , 교회 반주 10년")
                .price(50000)
                .part(Part.LIVE_SESSION)
                .skill(Skill.GUITAR)
                .genre(Genre.POP)
                .build();
    }

    @DisplayName(value = "필수값 일치하지 않으면 에러 발생")
    @Test
    void t2() {
        Assertions.assertThatThrownBy(() -> {
                    Article.builder()
                            .career("누구누구 세션 , 교회 반주 10년")
                            .price(50000)
                            .part(Part.LIVE_SESSION)
                            .skill(Skill.GUITAR)
                            .genre(Genre.POP)
                            .build();
                })
                .isInstanceOf(IllegalArgumentException.class);
    }
}