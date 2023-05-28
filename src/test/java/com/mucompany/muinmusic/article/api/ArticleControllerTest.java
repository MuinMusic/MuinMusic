package com.mucompany.muinmusic.article.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mucompany.muinmusic.article.Article;
import com.mucompany.muinmusic.article.api.dto.ArticleCreateDto;
import com.mucompany.muinmusic.article.repository.ArticleRepository;
import com.mucompany.muinmusic.domain.Genre;
import com.mucompany.muinmusic.domain.Part;
import com.mucompany.muinmusic.domain.Skill;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ArticleControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ArticleRepository articleRepository;

    @DisplayName(value = "articleCreateDto 유효하면 저장성공")
    @Test
    void t1() throws Exception {

        ArticleCreateDto articleCreateDto = ArticleCreateDto.builder()
                .title("10년차 드러머 레슨생 모집합니다")
                .career("가수 누구누구 서울투어 콘서트 등등")
                .price(100000)
                .part(Part.STUDIO_SESSION)
                .skill(Skill.DRUM)
                .genre(Genre.POP)
                .build();

        String part = String.valueOf(Part.STUDIO_SESSION);

        String requestBody = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(articleCreateDto);

        mockMvc.perform(post("/article").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..title").value(articleCreateDto.getTitle()))
                .andExpect(jsonPath("$..career").value(articleCreateDto.getCareer()))
                .andExpect(jsonPath("$..part").value(part))
                .andExpect(jsonPath("$..genre").value(articleCreateDto.getGenre().toString()));
    }

    @DisplayName(value = "articleId 유효하면 삭제 성공")
    @Test
    void t2() throws Exception {

        createArticle();

        Long articleId = 1L;

        mockMvc.perform(delete("/article/{articleId}", articleId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    private void createArticle() {
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
