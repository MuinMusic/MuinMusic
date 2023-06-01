package com.mucompany.muinmusic.jobseekerprofile.respository;

import com.mucompany.muinmusic.genre.Genre;
import com.mucompany.muinmusic.jobseekerprofile.JobSeekerProfile;
import com.mucompany.muinmusic.member.JobSeeker;
import com.mucompany.muinmusic.part.Part;
import com.mucompany.muinmusic.skill.Skill;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
public class JobSeekerProfileRepositoryTest {

    @Autowired
    private JobSeekerProfileRepository jobSeekerProfileRepository;

    @Autowired
    private EntityManager em;

    @Transactional(readOnly = true)
    @DisplayName(value = "필수값들 유효하면 작성 성공")
    @Test
    void t1() {
        JobSeeker jobSeeker = JobSeeker.builder()
                .name("son")
                .email("dp@naver.com")
                .password("1234")
                .university("없음")
                .major("vocal")
                .build();

        em.persist(jobSeeker);

        Part part = new Part();
        Part part2 = new Part();
        part.setType("live session");
        part2.setType("studio session");
        em.persist(part);
        em.persist(part2);

        List<Part> partList = new ArrayList<>();
        partList.add(part);
        partList.add(part2);


        Skill skill = new Skill();
        skill.setSkills("guitar");
        em.persist(skill);

        List<Skill> skillList = new ArrayList<>();
        skillList.add(skill);


        Genre genre1 = new Genre();
        genre1.setType("pop");
        Genre genre2 = new Genre();
        genre2.setType("pop");

        em.persist(genre1);
        em.persist(genre2);

        List<Genre> genreList = new ArrayList<>();
        genreList.add(genre1);
        genreList.add(genre2);


        JobSeekerProfile jobSeekerProfile = JobSeekerProfile.builder()
                .title("제목")
                .career("커리어")
                .jobSeeker(jobSeeker)
                .price(10000)
                .part(partList)
                .skill(skillList)
                .genre(genreList)
                .build();

        JobSeekerProfile saveJobSeekerProfile = jobSeekerProfileRepository.save(jobSeekerProfile);

        assertThat(saveJobSeekerProfile.getPrice()).isEqualTo(10000);
        assertThat(saveJobSeekerProfile.getSkill().size()).isEqualTo(1);
        assertThat(saveJobSeekerProfile.getGenre().size()).isEqualTo(2);
    }
}