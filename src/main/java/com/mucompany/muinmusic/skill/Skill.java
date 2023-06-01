package com.mucompany.muinmusic.skill;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@ToString
@NoArgsConstructor
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String instrument;

//    @ManyToOne
//    @JoinColumn(name = "job_seeker_profile_id")
//    private JobSeekerProfile jobSeekerProfile;

    public Skill(String instrument) {
        this.instrument = instrument;
    }
}
