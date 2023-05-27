package com.mucompany.muinmusic.member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.util.Assert;

@Entity
@Getter
@NoArgsConstructor
public class Member {

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    @NotNull
    private String email;

    @NotNull
    @Column(nullable = false)
    private String password;

    @NotNull
    @Column(nullable = false)
    private String name;

    private String university;

    private String major;

    @Builder
    public Member(Long id, String email, String password, String name, String university, String major) {
        Assert.notNull(email,"eamil은 필수값이여 합니다");
        Assert.notNull(password,"password는 필수값이여 합니다");
        Assert.notNull(name,"name은 필수값이여 합니다");

        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.university = university;
        this.major = major;
    }
}