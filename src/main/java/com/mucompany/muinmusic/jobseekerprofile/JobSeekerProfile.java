package com.mucompany.muinmusic.jobseekerprofile;

import com.mucompany.muinmusic.genre.Genre;
import com.mucompany.muinmusic.member.JobSeeker;
import com.mucompany.muinmusic.part.Part;
import com.mucompany.muinmusic.skill.Skill;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class JobSeekerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String career;

    @OneToOne
    @JoinColumn(name = "job_seeker_id")
    private JobSeeker jobSeeker;

    @Column(nullable = false)
    private int price;

    @OneToMany
    private List<Part> part;

    @OneToMany
    private List<Skill> skill;

    @OneToMany
    private List<Genre> genre;

    @Builder
    public JobSeekerProfile( String title, String career,JobSeeker jobSeeker, int price, List<Part> part, List<Skill> skill, List<Genre> genre) {
        Assert.notNull(title,"title 필수값이여 합니다");
        Assert.notNull(jobSeeker,"jobSeeker 필수값이여 합니다");
        Assert.notNull(price,"price 필수값이여 합니다");
        Assert.notNull(part,"part 필수값이여 합니다");
        Assert.notNull(skill,"skill 필수값이여 합니다");
        Assert.notNull(genre,"genre 필수값이여 합니다");

        this.title = title;
        this.career = career;
        this.jobSeeker = jobSeeker;
        this.price = price;
        this.part = part;
        this.skill = skill;
        this.genre = genre;
    }
}