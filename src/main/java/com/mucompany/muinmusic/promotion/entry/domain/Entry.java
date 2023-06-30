package com.mucompany.muinmusic.promotion.entry.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Entity
public class Entry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column
    private long memberId;

    @Column
    private String promotionCode;

    @Column
    private LocalDateTime enteredAt;

    protected Entry() {

    }

    public Entry(long memberId, String promotionCode, LocalDateTime requestedAt) {
        this.memberId = memberId;
        this.promotionCode = promotionCode;
        this.enteredAt = requestedAt;
    }
}
