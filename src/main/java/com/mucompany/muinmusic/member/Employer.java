package com.mucompany.muinmusic.member;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

@Entity
@Getter
@NoArgsConstructor
public class Employer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Builder
    public Employer(Long id, String email, String password, String name) {
        Assert.notNull(email,"email은 필수값이여 합니다");
        Assert.notNull(password,"password는 필수값이여 합니다");
        Assert.notNull(name,"name은 필수값이여 합니다");

        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
    }
}