package com.mucompany.muinmusic.promotion.entry.controller;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Positive;

public record EnterPromotionRequestDto(@Positive @JsonProperty("memberId") long memberId) {

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public EnterPromotionRequestDto {
        // NOTE 불변으로 만들기 위해 생성자에 @JsonProperty 를 붙여주기 위한 생성자
    }

}
