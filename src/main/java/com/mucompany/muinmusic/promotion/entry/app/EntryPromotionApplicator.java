package com.mucompany.muinmusic.promotion.entry.app;

import com.mucompany.muinmusic.promotion.entry.domain.Entry;
import com.mucompany.muinmusic.promotion.entry.domain.EntryRepository;
import org.springframework.stereotype.Component;

@Component
public class EntryPromotionApplicator {

    private final EntryRepository entryRepository;

    public EntryPromotionApplicator(EntryRepository entryRepository) {
        this.entryRepository = entryRepository;
    }

    public void apply(EnterPromotionRequest request) {
        var entry = new Entry(request.memberId(), request.promotionCode(), request.requestedAt());

        entryRepository.save(entry);

        // TODO: 응모완료 이벤트 발행
    }

}
