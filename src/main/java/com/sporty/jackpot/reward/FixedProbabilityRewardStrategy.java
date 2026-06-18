package com.sporty.jackpot.reward;

import com.sporty.jackpot.model.Jackpot;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("FIXED_PROBABILITY")
@RequiredArgsConstructor
public class FixedProbabilityRewardStrategy implements RewardStrategy {

    /** Probability expressed as a percentage, e.g. 5 means 5%. */
    @Value("${jackpot.reward.probability-rate:5}")
    private double probabilityRate;

    private final RandomProvider randomProvider;

    @Override
    public RewardResult evaluate(Jackpot jackpot) {
        double threshold = probabilityRate / 100.0;
        if (randomProvider.nextDouble() < threshold) {
            return RewardResult.win(jackpot.getCurrentPoolAmount());
        }
        return RewardResult.miss();
    }
}
