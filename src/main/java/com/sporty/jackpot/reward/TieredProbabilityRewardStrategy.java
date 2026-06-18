package com.sporty.jackpot.reward;

import com.sporty.jackpot.config.TieredProbabilityProperties;
import com.sporty.jackpot.model.Jackpot;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component("TIERED_PROBABILITY")
@RequiredArgsConstructor
public class TieredProbabilityRewardStrategy implements RewardStrategy {

    private static final double MAX_PROBABILITY = 100.0;

    private final TieredProbabilityProperties properties;
    private final RandomProvider randomProvider;

    @Override
    public RewardResult evaluate(Jackpot jackpot) {
        double probabilityRate = resolveRate(jackpot.getCurrentPoolAmount());

        if (probabilityRate >= MAX_PROBABILITY) {
            return RewardResult.win(jackpot.getCurrentPoolAmount());
        }

        double threshold = probabilityRate / 100.0;
        if (randomProvider.nextDouble() < threshold) {
            return RewardResult.win(jackpot.getCurrentPoolAmount());
        }
        return RewardResult.miss();
    }

    private double resolveRate(BigDecimal currentPoolAmount) {
        List<TieredProbabilityProperties.TierConfig> tiers = properties.getTieredProbabilities();
        for (TieredProbabilityProperties.TierConfig tier : tiers) {
            if (tier.getThreshold() == null || currentPoolAmount.compareTo(tier.getThreshold()) < 0) {
                return tier.getProbabilityRate();
            }
        }
        return tiers.get(tiers.size() - 1).getProbabilityRate();
    }
}
