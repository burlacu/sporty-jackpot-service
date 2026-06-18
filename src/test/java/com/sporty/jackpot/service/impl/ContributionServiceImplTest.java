package com.sporty.jackpot.service.impl;

import com.sporty.jackpot.dto.ContributionRequestDTO;
import com.sporty.jackpot.dto.ContributionResponseDTO;
import com.sporty.jackpot.exception.JackpotNotFoundException;
import com.sporty.jackpot.model.ContributionType;
import com.sporty.jackpot.model.Jackpot;
import com.sporty.jackpot.model.JackpotContribution;
import com.sporty.jackpot.model.RewardType;
import com.sporty.jackpot.repository.ContributionRepository;
import com.sporty.jackpot.repository.JackpotRepository;
import com.sporty.jackpot.strategy.ContributionStrategy;
import com.sporty.jackpot.strategy.ContributionStrategyFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContributionServiceImplTest {

    @Mock
    private ContributionRepository contributionRepository;

    @Mock
    private JackpotRepository jackpotRepository;

    @Mock
    private ContributionStrategyFactory strategyFactory;

    @Mock
    private ContributionStrategy strategy;

    @InjectMocks
    private ContributionServiceImpl contributionService;

    private Jackpot jackpot;
    private ContributionRequestDTO request;

    @BeforeEach
    void setUp() {
        jackpot = Jackpot.builder()
                .id(1L)
                .currentPoolAmount(new BigDecimal("1000.00"))
                .contributionType(ContributionType.PERCENTAGE)
                .rewardType(RewardType.FULL_POOL)
                .initialPoolAmount(new BigDecimal("1000.00"))
                .build();

        request = ContributionRequestDTO.builder()
                .betId(42L)
                .userId(7L)
                .stakeAmount(new BigDecimal("100.00"))
                .build();
    }

    @Test
    void addContribution_loadsJackpotAndResolvesStrategy() {
        when(jackpotRepository.findById(1L)).thenReturn(Optional.of(jackpot));
        when(strategyFactory.resolve(ContributionType.PERCENTAGE)).thenReturn(strategy);
        when(strategy.calculateContribution(any(), any())).thenReturn(new BigDecimal("5.0000"));
        when(contributionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        contributionService.addContribution(1L, request);

        verify(jackpotRepository).findById(1L);
        verify(strategyFactory).resolve(ContributionType.PERCENTAGE);
        verify(strategy).calculateContribution(new BigDecimal("100.00"), new BigDecimal("1000.00"));
    }

    @Test
    void addContribution_updatesJackpotPoolAmount() {
        when(jackpotRepository.findById(1L)).thenReturn(Optional.of(jackpot));
        when(strategyFactory.resolve(any())).thenReturn(strategy);
        when(strategy.calculateContribution(any(), any())).thenReturn(new BigDecimal("5.0000"));
        when(contributionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        contributionService.addContribution(1L, request);

        // pool 1000 + contribution 5 = 1005
        assertThat(jackpot.getCurrentPoolAmount()).isEqualByComparingTo("1005.0000");
        verify(jackpotRepository).save(jackpot);
    }

    @Test
    void addContribution_persistsAuditRecord() {
        when(jackpotRepository.findById(1L)).thenReturn(Optional.of(jackpot));
        when(strategyFactory.resolve(any())).thenReturn(strategy);
        when(strategy.calculateContribution(any(), any())).thenReturn(new BigDecimal("5.0000"));
        when(contributionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        contributionService.addContribution(1L, request);

        ArgumentCaptor<JackpotContribution> captor = ArgumentCaptor.forClass(JackpotContribution.class);
        verify(contributionRepository).save(captor.capture());

        JackpotContribution saved = captor.getValue();
        assertThat(saved.getBetId()).isEqualTo(42L);
        assertThat(saved.getUserId()).isEqualTo(7L);
        assertThat(saved.getJackpotId()).isEqualTo(1L);
        assertThat(saved.getStakeAmount()).isEqualByComparingTo("100.00");
        assertThat(saved.getContributionAmount()).isEqualByComparingTo("5.0000");
        assertThat(saved.getCurrentJackpotAmount()).isEqualByComparingTo("1005.0000");
    }

    @Test
    void addContribution_returnsPopulatedResponseDTO() {
        when(jackpotRepository.findById(1L)).thenReturn(Optional.of(jackpot));
        when(strategyFactory.resolve(any())).thenReturn(strategy);
        when(strategy.calculateContribution(any(), any())).thenReturn(new BigDecimal("5.0000"));
        when(contributionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ContributionResponseDTO result = contributionService.addContribution(1L, request);

        assertThat(result.getBetId()).isEqualTo(42L);
        assertThat(result.getUserId()).isEqualTo(7L);
        assertThat(result.getJackpotId()).isEqualTo(1L);
        assertThat(result.getStakeAmount()).isEqualByComparingTo("100.00");
        assertThat(result.getContributionAmount()).isEqualByComparingTo("5.0000");
        assertThat(result.getCurrentJackpotAmount()).isEqualByComparingTo("1005.0000");
    }

    @Test
    void addContribution_jackpotNotFound_throwsJackpotNotFoundException() {
        when(jackpotRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> contributionService.addContribution(99L, request))
                .isInstanceOf(JackpotNotFoundException.class)
                .hasMessageContaining("99");

        verifyNoInteractions(strategyFactory, contributionRepository);
    }
}
