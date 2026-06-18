package com.sporty.jackpot.strategy;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class PercentageContributionStrategyTest {

    private PercentageContributionStrategy strategy(String rate) {
        return new PercentageContributionStrategy(new BigDecimal(rate));
    }

    @Test
    void calculate_standardRate_returnsCorrectContribution() {
        // 100 * 5% = 5
        BigDecimal result = strategy("5").calculateContribution(
                new BigDecimal("100"), BigDecimal.ZERO);

        assertThat(result).isEqualByComparingTo("5.0000");
    }

    @Test
    void calculate_fractionalStake_returnsRoundedResult() {
        // 33.33 * 5% = 1.6665
        BigDecimal result = strategy("5").calculateContribution(
                new BigDecimal("33.33"), BigDecimal.ZERO);

        assertThat(result).isEqualByComparingTo("1.6665");
    }

    @Test
    void calculate_zeroStake_returnsZero() {
        BigDecimal result = strategy("5").calculateContribution(
                BigDecimal.ZERO, BigDecimal.ZERO);

        assertThat(result).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void calculate_100PercentRate_returnsFullStake() {
        // 50 * 100% = 50
        BigDecimal result = strategy("100").calculateContribution(
                new BigDecimal("50"), BigDecimal.ZERO);

        assertThat(result).isEqualByComparingTo("50.0000");
    }

    @Test
    void calculate_subPercentRate_returnsSmallContribution() {
        // 200 * 0.5% = 1
        BigDecimal result = strategy("0.5").calculateContribution(
                new BigDecimal("200"), BigDecimal.ZERO);

        assertThat(result).isEqualByComparingTo("1.0000");
    }

    @Test
    void calculate_roundsHalfUp() {
        // 1 * 3% = 0.03, scale 4 → 0.0300
        BigDecimal result = strategy("3").calculateContribution(
                new BigDecimal("1"), BigDecimal.ZERO);

        assertThat(result).isEqualByComparingTo("0.0300");
    }

    @Test
    void calculate_currentPoolAmountNotUsed() {
        // pool amount is irrelevant for percentage strategy
        BigDecimal withPool = strategy("5").calculateContribution(
                new BigDecimal("100"), new BigDecimal("99999"));
        BigDecimal withoutPool = strategy("5").calculateContribution(
                new BigDecimal("100"), BigDecimal.ZERO);

        assertThat(withPool).isEqualByComparingTo(withoutPool);
    }
}
