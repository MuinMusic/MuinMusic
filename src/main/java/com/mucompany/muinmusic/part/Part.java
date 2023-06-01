package com.mucompany.muinmusic.part;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Part {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String type;

//    @ManyToOne
//    @JoinColumn(name = "job_seeker_profile_id")
//    private JobSeekerProfile jobSeekerProfile;

    public Part(String type) {
        this.type = type;
    }
}
