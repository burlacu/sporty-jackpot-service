package com.sporty.jackpot.strategy;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Contributes a fixed monetary amount per bet, regardless of bet size or pool size.
 * The fixed amount is currently a default constant; configuration-based values are a future concern.
 */
@Component("FIXED")
public class FixedContributionStrategy implements ContributionStrategy {

    private static final BigDecimal DEFAULT_FIXED_AMOUNT = BigDecimal.ONE;

    @Override
    public BigDecimal calculateContribution(BigDecimal betAmount, BigDecimal currentPool) {
        return DEFAULT_FIXED_AMOUNT;
    }
}
