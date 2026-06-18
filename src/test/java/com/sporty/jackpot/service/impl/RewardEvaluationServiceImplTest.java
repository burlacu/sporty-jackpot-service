package com.sporty.jackpot.service.impl;

import com.sporty.jackpot.dto.EvaluateRewardRequest;
import com.sporty.jackpot.dto.EvaluateRewardResponse;
import com.sporty.jackpot.exception.ContributionNotFoundException;
import com.sporty.jackpot.exception.JackpotNotFoundException;
import com.sporty.jackpot.model.*;
import com.sporty.jackpot.repository.ContributionRepository;
import com.sporty.jackpot.repository.JackpotRepository;
import com.sporty.jackpot.repository.JackpotRewardRepository;
import com.sporty.jackpot.reward.RewardResult;
import com.sporty.jackpot.reward.RewardStrategy;
import com.sporty.jackpot.reward.RewardStrategyFactory;
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
class RewardEvaluationServiceImplTest {

    @Mock private ContributionRepository contributionRepository;
    @Mock private JackpotRepository jackpotRepository;
    @Mock private JackpotRewardRepository jackpotRewardRepository;
    @Mock private RewardStrategyFactory rewardStrategyFactory;
    @Mock private RewardStrategy rewardStrategy;

    @InjectMocks
    private RewardEvaluationServiceImpl service;

    private JackpotContribution contribution;
    private Jackpot jackpot;

    @BeforeEach
    void setUp() {
        jackpot = Jackpot.builder()
                .id(1L)
                .initialPoolAmount(new BigDecimal("1000.00"))
                .currentPoolAmount(new BigDecimal("15000.00"))
                .contributionType(ContributionType.PERCENTAGE)
                .rewardType(RewardType.FULL_POOL)
                .build();

        contribution = JackpotContribution.builder()
                .id(10L)
                .betId(42L)
                .userId(7L)
                .jackpotId(1L)
                .stakeAmount(new BigDecimal("100.00"))
                .contributionAmount(new BigDecimal("5.00"))
                .currentJackpotAmount(new BigDecimal("15000.00"))
                .build();
    }

    @Test
    void evaluate_winner_returnsWinnerResponseWithRewardAmount() {
        when(contributionRepository.findById(10L)).thenReturn(Optional.of(contribution));
        when(jackpotRepository.findById(1L)).thenReturn(Optional.of(jackpot));
        when(rewardStrategyFactory.resolve(RewardType.FULL_POOL)).thenReturn(rewardStrategy);
        when(rewardStrategy.evaluate(jackpot)).thenReturn(RewardResult.win(new BigDecimal("15000.00")));
        when(jackpotRewardRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        EvaluateRewardResponse response = service.evaluate(new EvaluateRewardRequest(10L));

        assertThat(response.isWinner()).isTrue();
        assertThat(response.getRewardAmount()).isEqualByComparingTo("15000.00");
    }

    @Test
    void evaluate_winner_persistsRewardRecord() {
        when(contributionRepository.findById(10L)).thenReturn(Optional.of(contribution));
        when(jackpotRepository.findById(1L)).thenReturn(Optional.of(jackpot));
        when(rewardStrategyFactory.resolve(any())).thenReturn(rewardStrategy);
        when(rewardStrategy.evaluate(any())).thenReturn(RewardResult.win(new BigDecimal("15000.00")));
        when(jackpotRewardRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.evaluate(new EvaluateRewardRequest(10L));

        ArgumentCaptor<JackpotReward> captor = ArgumentCaptor.forClass(JackpotReward.class);
        verify(jackpotRewardRepository).save(captor.capture());

        JackpotReward saved = captor.getValue();
        assertThat(saved.getBetId()).isEqualTo(42L);
        assertThat(saved.getUserId()).isEqualTo(7L);
        assertThat(saved.getJackpotId()).isEqualTo(1L);
        assertThat(saved.getRewardAmount()).isEqualByComparingTo("15000.00");
    }

    @Test
    void evaluate_winner_resetsJackpotPoolToInitialAmount() {
        when(contributionRepository.findById(10L)).thenReturn(Optional.of(contribution));
        when(jackpotRepository.findById(1L)).thenReturn(Optional.of(jackpot));
        when(rewardStrategyFactory.resolve(any())).thenReturn(rewardStrategy);
        when(rewardStrategy.evaluate(any())).thenReturn(RewardResult.win(new BigDecimal("15000.00")));
        when(jackpotRewardRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.evaluate(new EvaluateRewardRequest(10L));

        assertThat(jackpot.getCurrentPoolAmount()).isEqualByComparingTo("1000.00");
        verify(jackpotRepository).save(jackpot);
    }

    @Test
    void evaluate_noWinner_returnsNonWinnerResponse() {
        when(contributionRepository.findById(10L)).thenReturn(Optional.of(contribution));
        when(jackpotRepository.findById(1L)).thenReturn(Optional.of(jackpot));
        when(rewardStrategyFactory.resolve(any())).thenReturn(rewardStrategy);
        when(rewardStrategy.evaluate(any())).thenReturn(RewardResult.miss());

        EvaluateRewardResponse response = service.evaluate(new EvaluateRewardRequest(10L));

        assertThat(response.isWinner()).isFalse();
        assertThat(response.getRewardAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        verifyNoInteractions(jackpotRewardRepository);
        verify(jackpotRepository, never()).save(any());
    }

    @Test
    void evaluate_contributionNotFound_throwsContributionNotFoundException() {
        when(contributionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.evaluate(new EvaluateRewardRequest(99L)))
                .isInstanceOf(ContributionNotFoundException.class)
                .hasMessageContaining("99");

        verifyNoInteractions(jackpotRepository, rewardStrategyFactory);
    }

    @Test
    void evaluate_jackpotNotFound_throwsJackpotNotFoundException() {
        when(contributionRepository.findById(10L)).thenReturn(Optional.of(contribution));
        when(jackpotRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.evaluate(new EvaluateRewardRequest(10L)))
                .isInstanceOf(JackpotNotFoundException.class)
                .hasMessageContaining("1");

        verifyNoInteractions(rewardStrategyFactory, jackpotRewardRepository);
    }
}
