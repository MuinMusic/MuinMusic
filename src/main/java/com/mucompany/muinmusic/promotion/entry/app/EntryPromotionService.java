package com.mucompany.muinmusic.promotion.entry.app;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EntryPromotionService {

    private final EnterPromotionEligibilityValidator enterPromotionEligibilityValidator;
    private final EntryPromotionApplicator entryPromotionApplicator;


    public EntryPromotionService(
            EnterPromotionEligibilityValidator enterPromotionEligibilityValidator,
            EntryPromotionApplicator entryPromotionApplicator
    ) {
        this.enterPromotionEligibilityValidator = enterPromotionEligibilityValidator;
        this.entryPromotionApplicator = entryPromotionApplicator;
    }

    @Transactional
    public void enter(EnterPromotionRequest enterPromotionRequest) {
        enterPromotionEligibilityValidator.validate(enterPromotionRequest);

        entryPromotionApplicator.apply(enterPromotionRequest);
    }

}
