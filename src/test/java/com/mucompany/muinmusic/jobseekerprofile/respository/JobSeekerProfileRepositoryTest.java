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

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
public class JobSeekerProfileRepositoryTest {

    @Autowired
    private JobSeekerProfileRepository jobSeekerProfileRepository;

    @Autowired
    private EntityManager em;

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
        part.setType("live session");
        em.persist(part);


        Skill skill = new Skill();
        skill.setSkills("guitar");
        em.persist(skill);

        Genre genre = new Genre();
        genre.setType("pop");
        em.persist(genre);

        JobSeekerProfile jobSeekerProfile = JobSeekerProfile.builder()
                .title("제목")
                .career("커리어")
                .jobSeeker(jobSeeker)
                .price(10000)
                .part(part)
                .skill(skill)
                .genre(genre)
                .build();

        JobSeekerProfile saveJobSeekerProfile = jobSeekerProfileRepository.save(jobSeekerProfile);

        assertThat(saveJobSeekerProfile.getPrice()).isEqualTo(10000);
        assertThat(saveJobSeekerProfile.getSkill().getSkills()).isEqualTo(skill.getSkills());
        assertThat(saveJobSeekerProfile.getGenre().getType()).isEqualTo(genre.getType());
    }
}