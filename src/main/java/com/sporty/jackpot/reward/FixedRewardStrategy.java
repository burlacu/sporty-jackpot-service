package com.sporty.jackpot.reward;

import com.sporty.jackpot.model.Jackpot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component("FIXED")
public class FixedRewardStrategy implements RewardStrategy {

    private final BigDecimal fixedAmount;

    public FixedRewardStrategy(
            @Value("${jackpot.reward.fixed-amount:500}") BigDecimal fixedAmount) {
        this.fixedAmount = fixedAmount;
    }

    @Override
    public RewardResult evaluate(Jackpot jackpot) {
        return RewardResult.win(fixedAmount);
    }
}
