package com.mucompany.muinmusic.article;

import com.mucompany.muinmusic.domain.Genre;
import com.mucompany.muinmusic.domain.Part;
import com.mucompany.muinmusic.domain.Skill;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.util.Assert;

@Entity
@Getter
@NoArgsConstructor
public class Article {

    @Id
    @GeneratedValue
    @Column(name = "article_id")
    private Long id;

    @NotNull
    private Long userId;

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

    private int views;

    @Builder
    public Article(Long id, Long userId, String title, String career, int price, Part part, Skill skill, Genre genre,int views) {
        Assert.notNull(userId, "userId 필수값입니다");
        Assert.notNull(title, "title 필수값입니다");
        Assert.notNull(price, "price 필수값입니다");
        Assert.notNull(part, "part 필수값입니다");
        Assert.notNull(skill, "skill 필수값입니다");
        Assert.notNull(genre, "genre 필수값입니다");

        this.id = id;
        this.userId = userId;
        this.title = title;
        this.career = career;
        this.price = price;
        this.part = part;
        this.skill = skill;
        this.genre = genre;
        this.views = views;
    }

}
