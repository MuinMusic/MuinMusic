package com.mucompany.muinmusic.part.repository;

import com.mucompany.muinmusic.part.Part;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PartRepository extends JpaRepository<Part,Long> {
}
