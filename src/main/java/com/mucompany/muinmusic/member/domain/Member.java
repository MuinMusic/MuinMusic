package com.mucompany.muinmusic.member.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import org.springframework.util.Assert;

@Entity
@NoArgsConstructor
@Getter
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    public Member(String name, String address) {
        Assert.notNull(name,"name can not be null");
        Assert.notNull(address,"address can not be null");

        this.name = name;
        this.address = address;
    }
}
