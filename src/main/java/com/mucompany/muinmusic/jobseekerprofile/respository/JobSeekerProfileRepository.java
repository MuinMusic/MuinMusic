package com.mucompany.muinmusic.jobseekerprofile.respository;

import com.mucompany.muinmusic.jobseekerprofile.JobSeekerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.stereotype.Repository;

@Repository
public interface JobSeekerProfileRepository extends JpaRepository<JobSeekerProfile,Long> {

}
