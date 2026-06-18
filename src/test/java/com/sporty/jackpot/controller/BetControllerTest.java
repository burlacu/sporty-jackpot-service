package com.sporty.jackpot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sporty.jackpot.dto.BetAcknowledgement;
import com.sporty.jackpot.dto.BetRequest;
import com.sporty.jackpot.service.BetService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BetController.class)
class BetControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean  private BetService betService;

    @Test
    void receiveBet_validRequest_returns200WithAcknowledgement() throws Exception {
        BetRequest request = BetRequest.builder()
                .betId(1L).userId(2L).jackpotId(3L).amount(new BigDecimal("50.00"))
                .build();

        BetAcknowledgement ack = BetAcknowledgement.builder()
                .betId(1L).status("ACCEPTED").timestamp(LocalDateTime.now())
                .build();

        when(betService.processBet(any())).thenReturn(ack);

        mockMvc.perform(post("/api/v1/bets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.betId").value(1))
                .andExpect(jsonPath("$.status").value("ACCEPTED"));
    }

    @Test
    void receiveBet_duplicateBet_returns200WithAlreadyProcessed() throws Exception {
        BetRequest request = BetRequest.builder()
                .betId(1L).userId(2L).jackpotId(3L).amount(new BigDecimal("50.00"))
                .build();

        when(betService.processBet(any()))
                .thenReturn(BetAcknowledgement.builder()
                        .betId(1L).status("ALREADY_PROCESSED").timestamp(LocalDateTime.now())
                        .build());

        mockMvc.perform(post("/api/v1/bets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ALREADY_PROCESSED"));
    }

    @Test
    void receiveBet_missingBetId_returns400() throws Exception {
        BetRequest request = BetRequest.builder()
                .userId(2L).jackpotId(3L).amount(new BigDecimal("50.00"))
                .build();

        mockMvc.perform(post("/api/v1/bets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void receiveBet_zeroAmount_returns400() throws Exception {
        BetRequest request = BetRequest.builder()
                .betId(1L).userId(2L).jackpotId(3L).amount(BigDecimal.ZERO)
                .build();

        mockMvc.perform(post("/api/v1/bets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
