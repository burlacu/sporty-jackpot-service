package com.sporty.jackpot.service.impl;

import com.sporty.jackpot.dto.ContributionRequestDTO;
import com.sporty.jackpot.model.Bet;
import com.sporty.jackpot.service.ContributionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BetProcessingServiceImplTest {

    @Mock
    private ContributionService contributionService;

    @InjectMocks
    private BetProcessingServiceImpl betProcessingService;

    @Test
    void process_forwardsJackpotIdToContributionService() {
        Bet bet = Bet.builder()
                .betId(42L)
                .userId(7L)
                .jackpotId(5L)
                .amount(new BigDecimal("200.00"))
                .build();

        betProcessingService.process(bet);

        verify(contributionService).addContribution(eq(5L), any(ContributionRequestDTO.class));
    }

    @Test
    void process_mapsAllBetFieldsToContributionRequest() {
        Bet bet = Bet.builder()
                .betId(42L)
                .userId(7L)
                .jackpotId(5L)
                .amount(new BigDecimal("200.00"))
                .build();

        betProcessingService.process(bet);

        ArgumentCaptor<ContributionRequestDTO> captor = ArgumentCaptor.forClass(ContributionRequestDTO.class);
        verify(contributionService).addContribution(eq(5L), captor.capture());

        ContributionRequestDTO request = captor.getValue();
        assertThat(request.getBetId()).isEqualTo(42L);
        assertThat(request.getUserId()).isEqualTo(7L);
        assertThat(request.getStakeAmount()).isEqualByComparingTo("200.00");
    }
}
