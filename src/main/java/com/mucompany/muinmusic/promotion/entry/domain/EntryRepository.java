package com.mucompany.muinmusic.promotion.entry.domain;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EntryRepository extends CrudRepository<Entry, Long> {

    long countByMemberIdAndPromotionCode(long memberId, String code);

}
