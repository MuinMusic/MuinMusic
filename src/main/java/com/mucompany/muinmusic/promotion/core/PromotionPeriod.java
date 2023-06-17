package com.mucompany.muinmusic.promotion.core;

import com.google.common.collect.Range;
import jakarta.persistence.Embeddable;

import java.time.LocalDateTime;

@Embeddable
public class PromotionPeriod {

    private LocalDateTime startAt;
    private LocalDateTime endAt;

    protected PromotionPeriod() {

    }

    public PromotionPeriod(LocalDateTime startAt, LocalDateTime endAt) {
        this.startAt = startAt;
        this.endAt = endAt;
    }

    public boolean isInPromotionPeriod(LocalDateTime now) {
        return Range.closed(startAt, endAt).contains(now);
    }
}
