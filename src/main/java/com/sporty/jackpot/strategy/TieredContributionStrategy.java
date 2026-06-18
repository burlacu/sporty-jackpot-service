package com.sporty.jackpot.strategy;

import com.sporty.jackpot.config.TieredContributionProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TieredContributionStrategy implements ContributionStrategy {

    private final TieredContributionProperties properties;

    @Override
    public BigDecimal calculateContribution(BigDecimal stakeAmount, BigDecimal currentPoolAmount) {
        BigDecimal rate = resolveRate(currentPoolAmount);
        return stakeAmount.multiply(rate)
                .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
    }

    private BigDecimal resolveRate(BigDecimal currentPoolAmount) {
        List<TieredContributionProperties.TierConfig> tiers = properties.getTieredRates();
        for (TieredContributionProperties.TierConfig tier : tiers) {
            if (tier.getThreshold() == null || currentPoolAmount.compareTo(tier.getThreshold()) < 0) {
                return tier.getRate();
            }
        }
        return tiers.get(tiers.size() - 1).getRate();
    }
}
