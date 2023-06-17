package com.mucompany.muinmusic.promotion.entry.domain;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EntryPromotionRepository extends CrudRepository<EntryPromotion, Long> {
    Optional<EntryPromotion> findByCode(String promotionCodes);

}
