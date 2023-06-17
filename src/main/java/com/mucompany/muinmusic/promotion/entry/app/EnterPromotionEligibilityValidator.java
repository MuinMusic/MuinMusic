package com.mucompany.muinmusic.promotion.entry.app;


import com.mucompany.muinmusic.exception.MemberNotFoundException;
import com.mucompany.muinmusic.member.domain.Member;
import com.mucompany.muinmusic.member.domain.repository.MemberRepository;
import com.mucompany.muinmusic.promotion.core.PromotionNotActiveException;
import com.mucompany.muinmusic.promotion.core.PromotionNotFoundException;
import com.mucompany.muinmusic.promotion.entry.domain.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class EnterPromotionEligibilityValidator {

    private final EntryPromotionRepository entryPromotionRepository;
    private final MemberRepository memberRepository;
    private final EntryRepository entryRepository;

    public EnterPromotionEligibilityValidator(
            EntryPromotionRepository entryPromotionRepository,
            MemberRepository memberRepository,
            EntryRepository entryRepository
    ) {
        this.entryPromotionRepository = entryPromotionRepository;
        this.memberRepository = memberRepository;
        this.entryRepository = entryRepository;
    }

    public void validate(EnterPromotionRequest request) {
        validateIfPromotionActive(request.promotionCode(), request.requestedAt());
        validateIfMemberExists(request.memberId());
        validateIfAlreadyEntered(request.memberId(), request.promotionCode());
    }

    private void validateIfPromotionActive(String promotionCode, LocalDateTime requestedAt) {
        var entryPromotion = entryPromotionRepository.findByCode(promotionCode).orElseThrow(PromotionNotFoundException::new);

        boolean active = entryPromotion.isActive(requestedAt);
        if (!active) {
            throw new PromotionNotActiveException();
        }
    }

    private void validateIfMemberExists(long memberId) {
        var optionalMember = memberRepository.findById(memberId);
        if (optionalMember.isEmpty()) {
            throw new MemberNotFoundException();
        }
    }

    private void validateIfAlreadyEntered(long memberId, String promotionCode) {
        long entryCount = entryRepository.countByMemberIdAndPromotionCode(memberId, promotionCode);
        if (entryCount != 0) {
            throw new AlreadyEnteredPromotionException();
        }
    }

}
