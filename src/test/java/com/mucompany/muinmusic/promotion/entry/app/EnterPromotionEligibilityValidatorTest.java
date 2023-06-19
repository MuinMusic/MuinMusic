package com.mucompany.muinmusic.promotion.entry.app;

import com.mucompany.muinmusic.exception.MemberNotFoundException;
import com.mucompany.muinmusic.member.domain.Member;
import com.mucompany.muinmusic.member.domain.repository.MemberRepository;
import com.mucompany.muinmusic.promotion.core.PromotionNotActiveException;
import com.mucompany.muinmusic.promotion.core.PromotionNotFoundException;
import com.mucompany.muinmusic.promotion.core.PromotionPeriod;
import com.mucompany.muinmusic.promotion.entry.domain.AlreadyEnteredPromotionException;
import com.mucompany.muinmusic.promotion.entry.domain.Entry;
import com.mucompany.muinmusic.promotion.entry.domain.EntryPromotion;
import com.mucompany.muinmusic.promotion.entry.domain.EntryPromotionRepository;
import com.mucompany.muinmusic.promotion.entry.domain.EntryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class EnterPromotionEligibilityValidatorTest {

    private static final LocalDateTime NOW = LocalDateTime.now();
    private static final PromotionPeriod VALID_PERIOD = new PromotionPeriod(NOW.minusDays(1), NOW.plusDays(1));
    private static final EnterPromotionRequest ENTER_PROMOTION_REQUEST = new EnterPromotionRequest(1L, "ANY_PROMOTION_CODE", NOW);


    @Autowired
    EntryPromotionRepository entryPromotionRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    EntryRepository entryRepository;

    EnterPromotionEligibilityValidator sut;

    @BeforeEach
    void setUp() {
        sut = new EnterPromotionEligibilityValidator(entryPromotionRepository, memberRepository, entryRepository);
    }

    @Test
    @DisplayName("프로모션이 존재하지 않으면 PromotionNotFoundException 발생")
    void throwPromotionNotFoundException() {
        // given

        // when
        var result = assertThatThrownBy(() -> sut.validate(ENTER_PROMOTION_REQUEST));

        // then
        result.isInstanceOf(PromotionNotFoundException.class);
    }

    @Test
    @DisplayName("프로모션이 활성화되지 않았으면 PromotionNotActiveException 발생")
    void throwPromotionNotActiveException() {
        // given
        var finishedPromotion = new PromotionPeriod(NOW.minusDays(2), NOW.minusDays(1));
        entryPromotionRepository.save(new EntryPromotion("ANY_PROMOTION_CODE", "ANY_PROMOTION_NAME", finishedPromotion));

        // when
        var result = assertThatThrownBy(() -> sut.validate(ENTER_PROMOTION_REQUEST));

        // then
        result.isInstanceOf(PromotionNotActiveException.class);
    }

    @Test
    @DisplayName("회원이 존재하지 않으면 MemberNotFoundException 발생")
    void throwMemberNotFoundException() {
        // given
        entryPromotionRepository.save(new EntryPromotion("ANY_PROMOTION_CODE", "ANY_PROMOTION_NAME", VALID_PERIOD));

        // when
        var result = assertThatThrownBy(() -> sut.validate(ENTER_PROMOTION_REQUEST));

        // then
        result.isInstanceOf(MemberNotFoundException.class);
    }
    @Disabled
    @Test
    @DisplayName("프로모션에 이미 응모했으면 AlreadyEnteredPromotionException 발생")
    void throwAlreadyEnteredPromotionException() {
        // given
        entryPromotionRepository.save(new EntryPromotion("ANY_PROMOTION_CODE", "ANY_PROMOTION_NAME", VALID_PERIOD));
        memberRepository.save(new Member("ANY_MEMBER_NAME", "ANY_MEMBER_ADDRESS"));
        entryRepository.save(new Entry(1L, "ANY_PROMOTION_CODE", NOW));

        // when
        var result = assertThatThrownBy(() -> sut.validate(ENTER_PROMOTION_REQUEST));

        // then
        result.isInstanceOf(AlreadyEnteredPromotionException.class);
    }
}
