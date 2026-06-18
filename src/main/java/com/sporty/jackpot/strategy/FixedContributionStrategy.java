package com.sporty.jackpot.strategy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class FixedContributionStrategy implements ContributionStrategy {

    private final BigDecimal fixedAmount;

    public FixedContributionStrategy(
            @Value("${jackpot.contribution.fixed-amount:10}") BigDecimal fixedAmount) {
        this.fixedAmount = fixedAmount;
    }

    @Override
    public BigDecimal calculateContribution(BigDecimal stakeAmount, BigDecimal currentPoolAmount) {
        return fixedAmount;
    }
}
