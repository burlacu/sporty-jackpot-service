package com.sporty.jackpot.strategy;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Contributes a percentage of the bet amount to the jackpot pool.
 * The rate is currently a default constant; configuration-based values are a future concern.
 */
@Component("PERCENTAGE")
public class PercentageContributionStrategy implements ContributionStrategy {

    private static final BigDecimal DEFAULT_RATE = BigDecimal.valueOf(0.01);

    @Override
    public BigDecimal calculateContribution(BigDecimal betAmount, BigDecimal currentPool) {
        return betAmount.multiply(DEFAULT_RATE).setScale(4, RoundingMode.HALF_UP);
    }
}
