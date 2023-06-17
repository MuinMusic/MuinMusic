package com.mucompany.muinmusic.promotion.entry.controller;

import com.mucompany.muinmusic.promotion.entry.app.EnterPromotionRequest;
import com.mucompany.muinmusic.promotion.entry.app.EntryPromotionService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@Slf4j
@Validated
@RestController
public class EntryPromotionController {

    private final EntryPromotionService entryPromotionService;

    public EntryPromotionController(EntryPromotionService entryPromotionService) {
        this.entryPromotionService = entryPromotionService;
    }

    @PostMapping("/api/v1/promotions/{promotionCode}/enter")
    public EnterPromotionResponseDto enter(@PathVariable String promotionCode,
                                           @RequestBody @Valid EnterPromotionRequestDto requestDto) {
        var request = new EnterPromotionRequest(requestDto.memberId(), promotionCode, LocalDateTime.now());

        entryPromotionService.enter(request);

        return new EnterPromotionResponseDto(requestDto.memberId(), promotionCode);
    }

}
