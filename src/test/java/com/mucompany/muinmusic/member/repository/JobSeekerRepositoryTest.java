package com.mucompany.muinmusic.member.repository;

import com.mucompany.muinmusic.member.JobSeeker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
public class JobSeekerRepositoryTest {

    @Autowired
    private JobSeekerRepository jobSeekerRepository;

    @DisplayName(value = "member 필수값 유효하면 저장성공")
    @Test
    void t1() {

        JobSeeker jobSeeker = JobSeeker.builder()
                .name("son")
                .email("dp@naver.com")
                .password("1234")
                .university("없음")
                .major("vocal")
                .build();

        JobSeeker saveJobSeeker = jobSeekerRepository.save(jobSeeker);

        assertThat(saveJobSeeker.getName()).isEqualTo(jobSeeker.getName());
    }
}