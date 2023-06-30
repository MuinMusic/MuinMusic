package com.mucompany.muinmusic.promotion.entry.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mucompany.muinmusic.exception.MemberNotFoundException;
import com.mucompany.muinmusic.promotion.core.PromotionNotActiveException;
import com.mucompany.muinmusic.promotion.core.PromotionNotFoundException;
import com.mucompany.muinmusic.promotion.entry.app.EntryPromotionService;
import com.mucompany.muinmusic.promotion.entry.domain.AlreadyEnteredPromotionException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EntryPromotionController.class)
class EntryPromotionControllerTest {

    private static final EnterPromotionRequestDto VALID_REQUEST_DTO = new EnterPromotionRequestDto(1L);

    @Autowired
    private MockMvc mvc;

    @MockBean
    private EntryPromotionService entryPromotionService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("[200 OK] 응모 성공")
    void ok() throws Exception {
        // given

        // when
        var resultActions = mvc.perform(
                post("/api/v1/promotions/{promotionCode}/enter", "ANY_PROMOTION_CODE")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(VALID_REQUEST_DTO))
        );

        // then
        resultActions.andExpect(status().isOk());
    }

    @ParameterizedTest
    @ValueSource(longs = {0, -1, -100})
    @DisplayName("[400 Bad Request] 입력값이 유효하지 않은 경우")
    void badRequest1(long invalidMemberId) throws Exception {
        // given
        var request = new EnterPromotionRequestDto(invalidMemberId);

        // when
        var resultActions = mvc.perform(
                post("/api/v1/promotions/{promotionCode}/enter", "ANY_PROMOTION_CODE")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );

        // then
        resultActions.andExpect(status().isBadRequest());
    }

    @Disabled
    @Test
    @DisplayName("[404 Not Found] PromotionNotFoundException 발생")
    void notFound1() throws Exception {
        // given
        doThrow(new PromotionNotFoundException()).when(entryPromotionService).enter(any());

        // when
        var resultActions = mvc.perform(
                post("/api/v1/promotions/{promotionCode}/enter", "ANY_PROMOTION_CODE")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(VALID_REQUEST_DTO))
        );

        // then
        resultActions.andExpect(status().isNotFound());
    }

    @Disabled
    @Test
    @DisplayName("[404 Not Found] PromotionNotActiveException 발생")
    void notFound2() throws Exception {
        // given
        doThrow(new PromotionNotActiveException()).when(entryPromotionService).enter(any());

        // when
        var resultActions = mvc.perform(
                post("/api/v1/promotions/{promotionCode}/enter", "ANY_PROMOTION_CODE")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(VALID_REQUEST_DTO))
        );

        // then
        resultActions.andExpect(status().isNotFound());
        // TODO 응답 검증
    }

    @Test
    @DisplayName("[404 Not Found] MemberNotFoundException 발생")
    void notFound3() throws Exception {
        // given
        doThrow(new MemberNotFoundException()).when(entryPromotionService).enter(any());

        // when
        var resultActions = mvc.perform(
                post("/api/v1/promotions/{promotionCode}/enter", "ANY_PROMOTION_CODE")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(VALID_REQUEST_DTO))
        );

        // then
        resultActions.andExpect(status().isNotFound());
        // TODO 응답 검증
    }
//    @Disabled
//    @Test
//    @DisplayName("[409 Conflit] AlreadyEnteredPromotionException 발생")
//    void conflict1() throws Exception {
//        // given
//        doThrow(new AlreadyEnteredPromotionException()).when(entryPromotionService).enter(any());
//
//        // when
//        var resultActions = mvc.perform(
//                post("/api/v1/promotions/{promotionCode}/enter", "ANY_PROMOTION_CODE")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(VALID_REQUEST_DTO))
//        );
//
//        // then
//        resultActions.andExpect(status().isConflict());
//        // TODO 응답 검증
//    }

}
