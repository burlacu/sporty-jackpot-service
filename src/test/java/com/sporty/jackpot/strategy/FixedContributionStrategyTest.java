package com.sporty.jackpot.strategy;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class FixedContributionStrategyTest {

    @Test
    void calculate_returnsConfiguredFixedAmount() {
        FixedContributionStrategy strategy = new FixedContributionStrategy(new BigDecimal("10"));

        BigDecimal result = strategy.calculateContribution(new BigDecimal("500"), BigDecimal.ZERO);

        assertThat(result).isEqualByComparingTo("10");
    }

    @Test
    void calculate_stakeAmountDoesNotAffectResult() {
        FixedContributionStrategy strategy = new FixedContributionStrategy(new BigDecimal("10"));

        BigDecimal smallStake = strategy.calculateContribution(new BigDecimal("1"), BigDecimal.ZERO);
        BigDecimal largeStake = strategy.calculateContribution(new BigDecimal("99999"), BigDecimal.ZERO);

        assertThat(smallStake).isEqualByComparingTo("10");
        assertThat(largeStake).isEqualByComparingTo("10");
    }

    @Test
    void calculate_poolAmountDoesNotAffectResult() {
        FixedContributionStrategy strategy = new FixedContributionStrategy(new BigDecimal("10"));

        BigDecimal result = strategy.calculateContribution(new BigDecimal("100"), new BigDecimal("999999"));

        assertThat(result).isEqualByComparingTo("10");
    }

    @Test
    void calculate_customAmount_isConfigurable() {
        FixedContributionStrategy strategy = new FixedContributionStrategy(new BigDecimal("25.50"));

        BigDecimal result = strategy.calculateContribution(new BigDecimal("100"), BigDecimal.ZERO);

        assertThat(result).isEqualByComparingTo("25.50");
    }
}
