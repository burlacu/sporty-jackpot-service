package com.sporty.jackpot.reward;

import com.sporty.jackpot.model.ContributionType;
import com.sporty.jackpot.model.Jackpot;
import com.sporty.jackpot.model.RewardType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class PercentageRewardStrategyTest {

    @Test
    void evaluate_returnsPercentageOfPool() {
        // 10000 * 50% = 5000
        PercentageRewardStrategy strategy = new PercentageRewardStrategy(new BigDecimal("50"));

        RewardResult result = strategy.evaluate(jackpotWithPool("10000.00"));

        assertThat(result.rewardAmount()).isEqualByComparingTo("5000.0000");
    }

    @Test
    void evaluate_100PercentRate_returnsFullPool() {
        PercentageRewardStrategy strategy = new PercentageRewardStrategy(new BigDecimal("100"));

        RewardResult result = strategy.evaluate(jackpotWithPool("8000.00"));

        assertThat(result.rewardAmount()).isEqualByComparingTo("8000.0000");
    }

    @Test
    void evaluate_zeroPool_returnsZero() {
        PercentageRewardStrategy strategy = new PercentageRewardStrategy(new BigDecimal("50"));

        RewardResult result = strategy.evaluate(jackpotWithPool("0.00"));

        assertThat(result.rewardAmount()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void evaluate_fractionalPool_roundsHalfUp() {
        // 33.33 * 50% = 16.665 → rounds to 16.6650
        PercentageRewardStrategy strategy = new PercentageRewardStrategy(new BigDecimal("50"));

        RewardResult result = strategy.evaluate(jackpotWithPool("33.33"));

        assertThat(result.rewardAmount()).isEqualByComparingTo("16.6650");
    }

    @Test
    void evaluate_customRate_isConfigurable() {
        PercentageRewardStrategy strategy = new PercentageRewardStrategy(new BigDecimal("25"));

        RewardResult result = strategy.evaluate(jackpotWithPool("1000.00"));

        assertThat(result.rewardAmount()).isEqualByComparingTo("250.0000");
    }

    private Jackpot jackpotWithPool(String amount) {
        return Jackpot.builder()
                .id(1L)
                .currentPoolAmount(new BigDecimal(amount))
                .initialPoolAmount(new BigDecimal(amount))
                .contributionType(ContributionType.PERCENTAGE)
                .rewardType(RewardType.PERCENTAGE)
                .build();
    }
}
