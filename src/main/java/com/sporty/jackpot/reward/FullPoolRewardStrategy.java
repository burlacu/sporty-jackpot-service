package com.sporty.jackpot.reward;

import com.sporty.jackpot.model.Jackpot;
import org.springframework.stereotype.Component;

@Component("FULL_POOL")
public class FullPoolRewardStrategy implements RewardStrategy {

    @Override
    public RewardResult evaluate(Jackpot jackpot) {
        return RewardResult.win(jackpot.getCurrentPoolAmount());
    }
}
