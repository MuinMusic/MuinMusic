package com.mucompany.muinmusic.promotion.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PromotionPeriodTest {

    PromotionPeriod sut = new PromotionPeriod(LocalDateTime.of(2023, 1, 2, 0, 0, 0), LocalDateTime.of(2023, 1, 31, 23, 59, 59));

    @ParameterizedTest
    @ValueSource(strings = {"2023-01-02T00:00:00", "2023-01-31T23:59:59", "2023-01-15T12:00:00"})
    @DisplayName("프로모션 기간에 포함되면 유효한_기간")
    void valid(LocalDateTime now) {
        // given

        // when
        var result = sut.isInPromotionPeriod(now);

        // then
        assertTrue(result);
    }

    @ParameterizedTest
    @ValueSource(strings = {"2023-01-01T23:59:59", "2023-02-01T00:00:00"})
    @DisplayName("프로모션 기간을 벗어나면 유효하지_않음")
    void invalid(LocalDateTime now) {
        // given

        // when
        var result = sut.isInPromotionPeriod(now);

        // then
        assertFalse(result);
    }
}
