package com.mucompany.muinmusic.promotion.entry.app;

import java.time.LocalDateTime;

public record EnterPromotionRequest(long memberId, String promotionCode, LocalDateTime requestedAt) {

}
