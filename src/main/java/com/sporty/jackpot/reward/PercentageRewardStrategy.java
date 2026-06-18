package com.sporty.jackpot.reward;

import com.sporty.jackpot.model.Jackpot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component("PERCENTAGE")
public class PercentageRewardStrategy implements RewardStrategy {

    private final BigDecimal rate;

    public PercentageRewardStrategy(
            @Value("${jackpot.reward.percentage-rate:50}") BigDecimal rate) {
        this.rate = rate;
    }

    @Override
    public RewardResult evaluate(Jackpot jackpot) {
        BigDecimal rewardAmount = jackpot.getCurrentPoolAmount()
                .multiply(rate)
                .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
        return RewardResult.win(rewardAmount);
    }
}
