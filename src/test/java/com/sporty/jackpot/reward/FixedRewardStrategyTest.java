package com.sporty.jackpot.reward;

import com.sporty.jackpot.model.ContributionType;
import com.sporty.jackpot.model.Jackpot;
import com.sporty.jackpot.model.RewardType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class FixedRewardStrategyTest {

    @Test
    void evaluate_returnsConfiguredFixedAmount() {
        FixedRewardStrategy strategy = new FixedRewardStrategy(new BigDecimal("500"));

        RewardResult result = strategy.evaluate(anyJackpot("1000.00"));

        assertThat(result.rewardAmount()).isEqualByComparingTo("500");
    }

    @Test
    void evaluate_poolSizeDoesNotAffectResult() {
        FixedRewardStrategy strategy = new FixedRewardStrategy(new BigDecimal("500"));

        RewardResult smallPool = strategy.evaluate(anyJackpot("100.00"));
        RewardResult largePool = strategy.evaluate(anyJackpot("99999.00"));

        assertThat(smallPool.rewardAmount()).isEqualByComparingTo("500");
        assertThat(largePool.rewardAmount()).isEqualByComparingTo("500");
    }

    @Test
    void evaluate_customAmount_isConfigurable() {
        FixedRewardStrategy strategy = new FixedRewardStrategy(new BigDecimal("1234.56"));

        RewardResult result = strategy.evaluate(anyJackpot("5000.00"));

        assertThat(result.rewardAmount()).isEqualByComparingTo("1234.56");
    }

    private Jackpot anyJackpot(String poolAmount) {
        return Jackpot.builder()
                .id(1L)
                .currentPoolAmount(new BigDecimal(poolAmount))
                .initialPoolAmount(new BigDecimal(poolAmount))
                .contributionType(ContributionType.FIXED)
                .rewardType(RewardType.FIXED)
                .build();
    }
}
