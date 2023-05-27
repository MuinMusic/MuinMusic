package com.mucompany.muinmusic.member.domain;

import com.mucompany.muinmusic.member.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class MemberTest {

    @DisplayName(value = "필수값(email,name,password) 유효하면 빌드성공")
    @Test
    void t1() {

        Member member = Member.builder()
                .name("son")
                .email("dp@naver.com")
                .password("1234")
                .university("없음")
                .major("vocal")
                .build();
    }

    @DisplayName(value = "필수값(email,name,password) 없으면 예외발생")
    @Test()
    void t2() {

        assertThrows(IllegalArgumentException.class, () -> {
            Member member = Member.builder()
                    .name("son")
                    .email("dp@naver.com")
                    .university("없음")
                    .major("vocal")
                    .build();
        });
    }
}
