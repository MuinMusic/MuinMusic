package com.mucompany.muinmusic.jobSeekerProfile;

import com.mucompany.muinmusic.genre.Genre;
import com.mucompany.muinmusic.part.Part;
import com.mucompany.muinmusic.skill.Skill;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Entity
@Getter
@NoArgsConstructor
public class JobSeekerProfile {

    @Id
    @GeneratedValue
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

    public JobSeekerProfile(Long id, String title, String career, int price, Part part, Skill skill, Genre genre) {
        this.id = id;
        this.title = title;
        this.career = career;
        this.price = price;
        this.part = part;
        this.skill = skill;
        this.genre = genre;
    }
}