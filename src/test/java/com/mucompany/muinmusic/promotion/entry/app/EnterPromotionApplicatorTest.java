package com.mucompany.muinmusic.promotion.entry.app;

import com.mucompany.muinmusic.promotion.entry.domain.EntryRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

class EnterPromotionApplicatorTest {

    EntryRepository entryRepository = mock(EntryRepository.class);

    EntryPromotionApplicator sut = new EntryPromotionApplicator(entryRepository);

    @Test
    @DisplayName("응모에 성공하면 응모내역이 저장된다")
    void success() {
        //given
        var request = new EnterPromotionRequest(1L, "ANY_PROMOTION_CODE", LocalDateTime.now());

        // when
        sut.apply(request);

        // then
        verify(entryRepository).save(any());
    }
}
