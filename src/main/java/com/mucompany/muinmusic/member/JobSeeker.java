package com.mucompany.muinmusic.member;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import javax.validation.constraints.NotNull;

@Entity
@Getter
@NoArgsConstructor
public class JobSeeker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    private String university;

    private String major;

    @Builder
    public JobSeeker(Long id, String email, String password, String name, String university, String major) {
        Assert.notNull(email,"email은 필수값이여 합니다");
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