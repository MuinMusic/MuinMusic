package com.mucompany.muinmusic.article;

import com.mucompany.muinmusic.domain.Genre;
import com.mucompany.muinmusic.domain.Part;
import com.mucompany.muinmusic.domain.Skill;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;

@Entity
@Getter
@NoArgsConstructor
public class Article {

    @Id
    @GeneratedValue
    @Column(name = "article_id")
    private Long id;

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

    public Article(Long id, String title, String career, int price, Part part, Skill skill, Genre genre) {
        this.id = id;
        this.title = title;
        this.career = career;
        this.price = price;
        this.part = part;
        this.skill = skill;
        this.genre = genre;
    }

}
