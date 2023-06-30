package com.mucompany.muinmusic.promotion.entry.domain;

import com.mucompany.muinmusic.promotion.core.PromotionPeriod;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class EntryPromotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;

    private String title;

    @Embedded
    private PromotionPeriod period;

    @CreatedDate
    private LocalDateTime createdAt;

    protected EntryPromotion() {
    }

    public EntryPromotion(String code, String title, PromotionPeriod period) {
        this.code = code;
        this.title = title;
        this.period = period;
    }

    public boolean isActive(LocalDateTime now) {
        return period.isInPromotionPeriod(now);
    }

}
