package com.sporty.jackpot.reward;

import com.sporty.jackpot.config.TieredProbabilityProperties;
import com.sporty.jackpot.model.ContributionType;
import com.sporty.jackpot.model.Jackpot;
import com.sporty.jackpot.model.RewardType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TieredProbabilityRewardStrategyTest {

    @Mock
    private RandomProvider randomProvider;

    private TieredProbabilityRewardStrategy strategy;

    @BeforeEach
    void setUp() {
        // Pool < 10,000  → 1%
        // Pool < 50,000  → 5%
        // Pool < 100,000 → 10%
        // Pool >= 100,000 → 100% (guaranteed)
        strategy = new TieredProbabilityRewardStrategy(propertiesWith(
                tier("10000", 1.0),
                tier("50000", 5.0),
                tier("100000", 10.0),
                tier(null, 100.0)
        ), randomProvider);
    }

    @Test
    void evaluate_poolBelowFirstThreshold_applies1PercentProbability() {
        when(randomProvider.nextDouble()).thenReturn(0.009); // < 0.01

        RewardResult result = strategy.evaluate(jackpotWithPool("5000.00"));

        assertThat(result.triggered()).isTrue();
    }

    @Test
    void evaluate_poolBelowFirstThreshold_missWhenAbove1Percent() {
        when(randomProvider.nextDouble()).thenReturn(0.01); // == 0.01, not < 0.01

        RewardResult result = strategy.evaluate(jackpotWithPool("5000.00"));

        assertThat(result.triggered()).isFalse();
    }

    @Test
    void evaluate_poolAtFirstThreshold_applies5PercentProbability() {
        when(randomProvider.nextDouble()).thenReturn(0.04); // < 0.05

        RewardResult result = strategy.evaluate(jackpotWithPool("10000.00"));

        assertThat(result.triggered()).isTrue();
    }

    @Test
    void evaluate_poolBetweenThresholds_applies5PercentProbability() {
        when(randomProvider.nextDouble()).thenReturn(0.049); // < 0.05

        RewardResult result = strategy.evaluate(jackpotWithPool("25000.00"));

        assertThat(result.triggered()).isTrue();
    }

    @Test
    void evaluate_poolAtSecondThreshold_applies10PercentProbability() {
        when(randomProvider.nextDouble()).thenReturn(0.09); // < 0.10

        RewardResult result = strategy.evaluate(jackpotWithPool("50000.00"));

        assertThat(result.triggered()).isTrue();
    }

    @Test
    void evaluate_poolAtMaxThreshold_guaranteesWin() {
        // 100% probability — randomProvider must NOT be called
        RewardResult result = strategy.evaluate(jackpotWithPool("100000.00"));

        assertThat(result.triggered()).isTrue();
        assertThat(result.rewardAmount()).isEqualByComparingTo("100000.00");
        verifyNoInteractions(randomProvider);
    }

    @Test
    void evaluate_poolAboveMaxThreshold_guaranteesWin() {
        RewardResult result = strategy.evaluate(jackpotWithPool("999999.00"));

        assertThat(result.triggered()).isTrue();
        verifyNoInteractions(randomProvider);
    }

    @Test
    void evaluate_win_returnsCurrentPoolAmount() {
        when(randomProvider.nextDouble()).thenReturn(0.0);

        RewardResult result = strategy.evaluate(jackpotWithPool("42000.00"));

        assertThat(result.triggered()).isTrue();
        assertThat(result.rewardAmount()).isEqualByComparingTo("42000.00");
    }

    @Test
    void evaluate_miss_returnsZeroAmount() {
        when(randomProvider.nextDouble()).thenReturn(0.99); // far above any threshold

        RewardResult result = strategy.evaluate(jackpotWithPool("5000.00"));

        assertThat(result.triggered()).isFalse();
        assertThat(result.rewardAmount()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void evaluate_customTiers_areConfigurable() {
        TieredProbabilityRewardStrategy custom = new TieredProbabilityRewardStrategy(
                propertiesWith(tier("1000", 50.0), tier(null, 100.0)),
                randomProvider
        );
        when(randomProvider.nextDouble()).thenReturn(0.49); // < 0.50

        RewardResult result = custom.evaluate(jackpotWithPool("500.00"));

        assertThat(result.triggered()).isTrue();
    }

    // --- helpers ---

    private TieredProbabilityProperties propertiesWith(TieredProbabilityProperties.TierConfig... tiers) {
        TieredProbabilityProperties props = new TieredProbabilityProperties();
        props.setTieredProbabilities(List.of(tiers));
        return props;
    }

    private TieredProbabilityProperties.TierConfig tier(String threshold, double probabilityRate) {
        TieredProbabilityProperties.TierConfig config = new TieredProbabilityProperties.TierConfig();
        config.setThreshold(threshold != null ? new BigDecimal(threshold) : null);
        config.setProbabilityRate(probabilityRate);
        return config;
    }

    private Jackpot jackpotWithPool(String amount) {
        return Jackpot.builder()
                .id(1L)
                .currentPoolAmount(new BigDecimal(amount))
                .initialPoolAmount(new BigDecimal(amount))
                .contributionType(ContributionType.PERCENTAGE)
                .rewardType(RewardType.TIERED_PROBABILITY)
                .build();
    }
}
