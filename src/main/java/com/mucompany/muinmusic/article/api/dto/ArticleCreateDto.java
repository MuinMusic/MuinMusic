package com.mucompany.muinmusic.article.api.dto;

import com.mucompany.muinmusic.domain.Genre;
import com.mucompany.muinmusic.domain.Part;
import com.mucompany.muinmusic.domain.Skill;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;

@Getter
@NoArgsConstructor
public class ArticleCreateDto {

    @NotNull
    private String title;

    private String career;

    @NotNull
    private int price;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Part part;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Skill skill;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Genre genre;

    @Builder
    public ArticleCreateDto(String title, String career, int price, Part part, Skill skill, Genre genre) {
        this.title = title;
        this.career = career;
        this.price = price;
        this.part = part;
        this.skill = skill;
        this.genre = genre;
    }
}