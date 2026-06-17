package com.sporty.jackpot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sporty.jackpot.dto.JackpotDTO;
import com.sporty.jackpot.model.ContributionType;
import com.sporty.jackpot.model.RewardType;
import com.sporty.jackpot.service.JackpotService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(JackpotController.class)
class JackpotControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JackpotService jackpotService;

    @Test
    void createJackpot_shouldReturn201() throws Exception {
        JackpotDTO request = JackpotDTO.builder()
                .initialPoolAmount(BigDecimal.valueOf(1000))
                .contributionType(ContributionType.PERCENTAGE)
                .rewardType(RewardType.FULL_POOL)
                .build();

        JackpotDTO response = JackpotDTO.builder()
                .id(1L)
                .initialPoolAmount(BigDecimal.valueOf(1000))
                .currentPoolAmount(BigDecimal.valueOf(1000))
                .contributionType(ContributionType.PERCENTAGE)
                .rewardType(RewardType.FULL_POOL)
                .build();

        when(jackpotService.createJackpot(any(JackpotDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/jackpots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.contributionType").value("PERCENTAGE"))
                .andExpect(jsonPath("$.rewardType").value("FULL_POOL"));
    }

    @Test
    void getAllJackpots_shouldReturn200WithList() throws Exception {
        JackpotDTO dto = JackpotDTO.builder()
                .id(1L)
                .initialPoolAmount(BigDecimal.valueOf(1000))
                .currentPoolAmount(BigDecimal.valueOf(1100))
                .contributionType(ContributionType.FIXED)
                .rewardType(RewardType.FULL_POOL)
                .build();

        when(jackpotService.getAllJackpots()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/v1/jackpots"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].contributionType").value("FIXED"));
    }

    @Test
    void createJackpot_withMissingRequiredFields_shouldReturn400() throws Exception {
        JackpotDTO request = JackpotDTO.builder().build();

        mockMvc.perform(post("/api/v1/jackpots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
