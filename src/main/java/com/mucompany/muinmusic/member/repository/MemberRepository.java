package com.mucompany.muinmusic.member.repository;

import com.mucompany.muinmusic.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

}
