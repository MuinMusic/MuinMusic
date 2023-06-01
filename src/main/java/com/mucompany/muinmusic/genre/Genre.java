package com.mucompany.muinmusic.genre;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Genre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String type;

//    @ManyToOne
//    @JoinColumn(name = "job_seeker_profile_id")
//    private JobSeekerProfile jobSeekerProfile;


    public Genre(String type) {
        this.type = type;
    }
}
