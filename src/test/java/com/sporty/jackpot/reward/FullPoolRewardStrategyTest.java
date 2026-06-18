package com.sporty.jackpot.reward;

import com.sporty.jackpot.model.ContributionType;
import com.sporty.jackpot.model.Jackpot;
import com.sporty.jackpot.model.RewardType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class FullPoolRewardStrategyTest {

    private final FullPoolRewardStrategy strategy = new FullPoolRewardStrategy();

    @Test
    void evaluate_returnsEntirePoolAmount() {
        Jackpot jackpot = jackpotWithPool("10000.00");

        RewardResult result = strategy.evaluate(jackpot);

        assertThat(result.triggered()).isTrue();
        assertThat(result.rewardAmount()).isEqualByComparingTo("10000.00");
    }

    @Test
    void evaluate_zeroPool_returnsZero() {
        Jackpot jackpot = jackpotWithPool("0.00");

        RewardResult result = strategy.evaluate(jackpot);

        assertThat(result.triggered()).isTrue();
        assertThat(result.rewardAmount()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void evaluate_fractionalPool_returnsPreciseAmount() {
        Jackpot jackpot = jackpotWithPool("9999.9999");

        RewardResult result = strategy.evaluate(jackpot);

        assertThat(result.triggered()).isTrue();
        assertThat(result.rewardAmount()).isEqualByComparingTo("9999.9999");
    }

    private Jackpot jackpotWithPool(String amount) {
        return Jackpot.builder()
                .id(1L)
                .currentPoolAmount(new BigDecimal(amount))
                .initialPoolAmount(new BigDecimal(amount))
                .contributionType(ContributionType.PERCENTAGE)
                .rewardType(RewardType.FULL_POOL)
                .build();
    }
}
