package com.mucompany.muinmusic.member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import javax.validation.constraints.NotNull;

@Entity
@Getter
@NoArgsConstructor
public class Employer {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    @Column(nullable = false)
    private String email;

    @NotNull
    @Column(nullable = false)
    private String password;

    @NotNull
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