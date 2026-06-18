package com.sporty.jackpot.strategy;

import com.sporty.jackpot.config.TieredContributionProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TieredContributionStrategyTest {

    private TieredContributionStrategy strategy;

    @BeforeEach
    void setUp() {
        // Pool < 10,000  → 10%
        // Pool < 50,000  → 5%
        // Pool >= 50,000 → 2%
        strategy = new TieredContributionStrategy(propertiesWith(
                tier("10000", "10"),
                tier("50000", "5"),
                tier(null, "2")
        ));
    }

    @Test
    void calculate_poolBelowFirstThreshold_appliesHighestRate() {
        // pool = 5,000 < 10,000 → 10%
        BigDecimal result = strategy.calculateContribution(new BigDecimal("100"), new BigDecimal("5000"));

        assertThat(result).isEqualByComparingTo("10.0000");
    }

    @Test
    void calculate_zeroPool_appliesFirstTierRate() {
        // pool = 0 < 10,000 → 10%
        BigDecimal result = strategy.calculateContribution(new BigDecimal("100"), BigDecimal.ZERO);

        assertThat(result).isEqualByComparingTo("10.0000");
    }

    @Test
    void calculate_poolAtFirstThreshold_appliesSecondRate() {
        // pool = 10,000, not < 10,000 → next tier: 10,000 < 50,000 → 5%
        BigDecimal result = strategy.calculateContribution(new BigDecimal("100"), new BigDecimal("10000"));

        assertThat(result).isEqualByComparingTo("5.0000");
    }

    @Test
    void calculate_poolBetweenThresholds_appliesMiddleRate() {
        // pool = 25,000 < 50,000 → 5%
        BigDecimal result = strategy.calculateContribution(new BigDecimal("100"), new BigDecimal("25000"));

        assertThat(result).isEqualByComparingTo("5.0000");
    }

    @Test
    void calculate_poolAtSecondThreshold_appliesFallbackRate() {
        // pool = 50,000, not < 50,000 → fallback → 2%
        BigDecimal result = strategy.calculateContribution(new BigDecimal("100"), new BigDecimal("50000"));

        assertThat(result).isEqualByComparingTo("2.0000");
    }

    @Test
    void calculate_poolAboveAllThresholds_appliesFallbackRate() {
        // pool = 100,000 → 2%
        BigDecimal result = strategy.calculateContribution(new BigDecimal("100"), new BigDecimal("100000"));

        assertThat(result).isEqualByComparingTo("2.0000");
    }

    @Test
    void calculate_customThresholds_areConfigurable() {
        TieredContributionStrategy custom = new TieredContributionStrategy(propertiesWith(
                tier("1000", "20"),
                tier(null, "1")
        ));

        BigDecimal below = custom.calculateContribution(new BigDecimal("100"), new BigDecimal("500"));
        BigDecimal above = custom.calculateContribution(new BigDecimal("100"), new BigDecimal("2000"));

        assertThat(below).isEqualByComparingTo("20.0000");
        assertThat(above).isEqualByComparingTo("1.0000");
    }

    @Test
    void calculate_usesBigDecimalPrecision() {
        // 33.33 * 5% = 1.6665
        BigDecimal result = strategy.calculateContribution(new BigDecimal("33.33"), new BigDecimal("25000"));

        assertThat(result).isEqualByComparingTo("1.6665");
    }

    // --- helpers ---

    private TieredContributionProperties propertiesWith(TieredContributionProperties.TierConfig... tiers) {
        TieredContributionProperties props = new TieredContributionProperties();
        props.setTieredRates(List.of(tiers));
        return props;
    }

    private TieredContributionProperties.TierConfig tier(String threshold, String rate) {
        TieredContributionProperties.TierConfig config = new TieredContributionProperties.TierConfig();
        config.setThreshold(threshold != null ? new BigDecimal(threshold) : null);
        config.setRate(new BigDecimal(rate));
        return config;
    }
}
