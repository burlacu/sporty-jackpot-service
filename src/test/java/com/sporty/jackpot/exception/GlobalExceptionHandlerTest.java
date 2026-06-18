package com.sporty.jackpot.exception;

import com.sporty.jackpot.controller.JackpotController;
import com.sporty.jackpot.filter.CorrelationIdFilter;
import com.sporty.jackpot.service.JackpotService;
import com.sporty.jackpot.service.RewardEvaluationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(JackpotController.class)
@Import(CorrelationIdFilter.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JackpotService jackpotService;

    @MockBean
    private RewardEvaluationService rewardEvaluationService;

    @Test
    void jackpotNotFound_returns404WithErrorCode() throws Exception {
        when(jackpotService.getJackpotById(99L))
                .thenThrow(new JackpotNotFoundException("Jackpot not found with id: 99"));

        mockMvc.perform(get("/api/v1/jackpots/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("JACKPOT_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Jackpot not found with id: 99"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void contributionNotFound_returns404WithErrorCode() throws Exception {
        when(rewardEvaluationService.evaluate(any()))
                .thenThrow(new ContributionNotFoundException("Contribution not found with id: 5"));

        mockMvc.perform(post("/api/v1/jackpots/evaluate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"contributionId\": 5}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("CONTRIBUTION_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Contribution not found with id: 5"));
    }

    @Test
    void illegalArgument_returns400WithErrorCode() throws Exception {
        when(rewardEvaluationService.evaluate(any()))
                .thenThrow(new IllegalArgumentException("No reward strategy registered for type: UNKNOWN"));

        mockMvc.perform(post("/api/v1/jackpots/evaluate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"contributionId\": 1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("INVALID_ARGUMENT"));
    }

    @Test
    void validationFailure_returns400WithDetailsAndErrorCode() throws Exception {
        mockMvc.perform(post("/api/v1/jackpots/evaluate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.details.contributionId").exists());
    }

    @Test
    void correlationId_propagatedFromRequestHeader() throws Exception {
        when(jackpotService.getJackpotById(99L))
                .thenThrow(new JackpotNotFoundException("Jackpot not found with id: 99"));

        mockMvc.perform(get("/api/v1/jackpots/99")
                        .header("X-Correlation-ID", "test-correlation-123"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.correlationId").value("test-correlation-123"))
                .andExpect(header().string("X-Correlation-ID", "test-correlation-123"));
    }

    @Test
    void correlationId_generatedWhenNotProvided() throws Exception {
        when(jackpotService.getJackpotById(99L))
                .thenThrow(new JackpotNotFoundException("Jackpot not found with id: 99"));

        mockMvc.perform(get("/api/v1/jackpots/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.correlationId").exists())
                .andExpect(header().exists("X-Correlation-ID"));
    }

    @Test
    void unexpectedException_returns500WithGenericMessage() throws Exception {
        when(jackpotService.getAllJackpots())
                .thenThrow(new RuntimeException("Unexpected DB failure"));

        mockMvc.perform(get("/api/v1/jackpots"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.errorCode").value("INTERNAL_ERROR"))
                .andExpect(jsonPath("$.message").value("An unexpected error occurred"));
    }
}
