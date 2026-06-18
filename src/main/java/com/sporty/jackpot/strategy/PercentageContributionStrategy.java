package com.sporty.jackpot.strategy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class PercentageContributionStrategy implements ContributionStrategy {

    private final BigDecimal rate;

    public PercentageContributionStrategy(
            @Value("${jackpot.contribution.percentage-rate:5}") BigDecimal rate) {
        this.rate = rate;
    }

    @Override
    public BigDecimal calculateContribution(BigDecimal stakeAmount, BigDecimal currentPoolAmount) {
        return stakeAmount.multiply(rate)
                .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
    }
}
