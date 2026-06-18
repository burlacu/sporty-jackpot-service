package com.sporty.jackpot.reward;

import com.sporty.jackpot.model.ContributionType;
import com.sporty.jackpot.model.Jackpot;
import com.sporty.jackpot.model.RewardType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FixedProbabilityRewardStrategyTest {

    @Mock
    private RandomProvider randomProvider;

    @InjectMocks
    private FixedProbabilityRewardStrategy strategy;

    @Test
    void evaluate_belowThreshold_triggersWin() {
        setProbabilityRate(5.0); // threshold = 0.05
        when(randomProvider.nextDouble()).thenReturn(0.04);

        RewardResult result = strategy.evaluate(jackpotWithPool("10000.00"));

        assertThat(result.triggered()).isTrue();
        assertThat(result.rewardAmount()).isEqualByComparingTo("10000.00");
    }

    @Test
    void evaluate_atThreshold_doesNotTrigger() {
        setProbabilityRate(5.0); // threshold = 0.05
        when(randomProvider.nextDouble()).thenReturn(0.05);

        RewardResult result = strategy.evaluate(jackpotWithPool("10000.00"));

        assertThat(result.triggered()).isFalse();
        assertThat(result.rewardAmount()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void evaluate_aboveThreshold_doesNotTrigger() {
        setProbabilityRate(5.0); // threshold = 0.05
        when(randomProvider.nextDouble()).thenReturn(0.99);

        RewardResult result = strategy.evaluate(jackpotWithPool("10000.00"));

        assertThat(result.triggered()).isFalse();
        assertThat(result.rewardAmount()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void evaluate_win_returnsCurrentPoolAmount() {
        setProbabilityRate(10.0);
        when(randomProvider.nextDouble()).thenReturn(0.09);

        RewardResult result = strategy.evaluate(jackpotWithPool("75000.00"));

        assertThat(result.triggered()).isTrue();
        assertThat(result.rewardAmount()).isEqualByComparingTo("75000.00");
    }

    @Test
    void evaluate_probabilityRate_isConfigurable() {
        setProbabilityRate(50.0); // threshold = 0.50
        when(randomProvider.nextDouble()).thenReturn(0.49);

        RewardResult result = strategy.evaluate(jackpotWithPool("1000.00"));

        assertThat(result.triggered()).isTrue();
    }

    @Test
    void evaluate_zeroProbability_neverTriggers() {
        setProbabilityRate(0.0); // threshold = 0.0
        when(randomProvider.nextDouble()).thenReturn(0.0);

        RewardResult result = strategy.evaluate(jackpotWithPool("10000.00"));

        assertThat(result.triggered()).isFalse();
    }

    private void setProbabilityRate(double rate) {
        ReflectionTestUtils.setField(strategy, "probabilityRate", rate);
    }

    private Jackpot jackpotWithPool(String amount) {
        return Jackpot.builder()
                .id(1L)
                .currentPoolAmount(new BigDecimal(amount))
                .initialPoolAmount(new BigDecimal(amount))
                .contributionType(ContributionType.PERCENTAGE)
                .rewardType(RewardType.FIXED_PROBABILITY)
                .build();
    }
}
