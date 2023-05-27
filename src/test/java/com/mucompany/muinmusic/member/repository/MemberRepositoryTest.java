package com.mucompany.muinmusic.member.repository;

import com.mucompany.muinmusic.member.Member;
import com.mucompany.muinmusic.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @DisplayName(value = "member 필수값 유효하면 저장성공")
    @Test
    void t1() {

        Member member = Member.builder()
                .name("son")
                .email("dp@naver.com")
                .password("1234")
                .university("없음")
                .major("vocal")
                .build();

        Member saveMember = memberRepository.save(member);

        assertThat(saveMember.getName()).isEqualTo(member.getName());
    }
}