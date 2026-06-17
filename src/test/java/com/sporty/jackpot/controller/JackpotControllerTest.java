package com.sporty.jackpot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sporty.jackpot.dto.JackpotDTO;
import com.sporty.jackpot.model.JackpotStatus;
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
                .name("Mega Jackpot")
                .status(JackpotStatus.ACTIVE)
                .build();

        JackpotDTO response = JackpotDTO.builder()
                .id(1L)
                .name("Mega Jackpot")
                .totalAmount(BigDecimal.ZERO)
                .status(JackpotStatus.ACTIVE)
                .build();

        when(jackpotService.createJackpot(any(JackpotDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/jackpots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Mega Jackpot"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void getAllJackpots_shouldReturn200WithList() throws Exception {
        JackpotDTO dto = JackpotDTO.builder()
                .id(1L)
                .name("Mega Jackpot")
                .totalAmount(BigDecimal.TEN)
                .status(JackpotStatus.ACTIVE)
                .build();

        when(jackpotService.getAllJackpots()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/v1/jackpots"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Mega Jackpot"));
    }

    @Test
    void createJackpot_withMissingName_shouldReturn400() throws Exception {
        JackpotDTO request = JackpotDTO.builder()
                .status(JackpotStatus.ACTIVE)
                .build();

        mockMvc.perform(post("/api/v1/jackpots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
