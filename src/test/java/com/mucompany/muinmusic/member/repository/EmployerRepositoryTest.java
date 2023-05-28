package com.mucompany.muinmusic.member.repository;

import com.mucompany.muinmusic.member.Employer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class EmployerRepositoryTest {

    @Autowired
    private EmployerRepository employerRepository;

    @DisplayName(value = "member 필수값 유효하면 저장성공")
    @Test
    void t1() {

        Employer employer = Employer.builder()
                .name("son")
                .email("dp@naver.com")
                .password("1234")
                .build();

        Employer saveEmployer = employerRepository.save(employer);

        assertThat(saveEmployer.getName()).isEqualTo(employer.getName());
    }
}
