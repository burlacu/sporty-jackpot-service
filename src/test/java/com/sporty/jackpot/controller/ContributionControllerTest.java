package com.sporty.jackpot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sporty.jackpot.dto.ContributionRequestDTO;
import com.sporty.jackpot.dto.ContributionResponseDTO;
import com.sporty.jackpot.service.ContributionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ContributionController.class)
class ContributionControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean  private ContributionService contributionService;

    private ContributionResponseDTO response() {
        return ContributionResponseDTO.builder()
                .id(1L).betId(10L).userId(5L).jackpotId(2L)
                .stakeAmount(new BigDecimal("100.00"))
                .contributionAmount(new BigDecimal("5.00"))
                .currentJackpotAmount(new BigDecimal("1005.00"))
                .build();
    }

    @Test
    void addContribution_returns201WithBody() throws Exception {
        ContributionRequestDTO request = ContributionRequestDTO.builder()
                .betId(10L).userId(5L).stakeAmount(new BigDecimal("100.00")).build();

        when(contributionService.addContribution(eq(2L), any())).thenReturn(response());

        mockMvc.perform(post("/api/v1/jackpots/2/contributions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.contributionAmount").value(5.0));
    }

    @Test
    void addContribution_missingFields_returns400() throws Exception {
        mockMvc.perform(post("/api/v1/jackpots/2/contributions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getContributionsByJackpot_returns200WithList() throws Exception {
        when(contributionService.getContributionsByJackpot(2L)).thenReturn(List.of(response()));

        mockMvc.perform(get("/api/v1/jackpots/2/contributions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].jackpotId").value(2));
    }

    @Test
    void getContributionById_returns200() throws Exception {
        when(contributionService.getContributionById(1L)).thenReturn(response());

        mockMvc.perform(get("/api/v1/contributions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getContributionsByUser_returns200WithList() throws Exception {
        when(contributionService.getContributionsByUser(5L)).thenReturn(List.of(response()));

        mockMvc.perform(get("/api/v1/users/5/contributions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].userId").value(5));
    }
}
