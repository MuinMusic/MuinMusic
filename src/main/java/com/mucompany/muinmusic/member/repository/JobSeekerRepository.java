package com.mucompany.muinmusic.member.repository;

import com.mucompany.muinmusic.member.JobSeeker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.stereotype.Repository;

@Repository
public interface JobSeekerRepository extends JpaRepository<JobSeeker, Long> {

}
