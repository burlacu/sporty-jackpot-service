package com.sporty.jackpot.reward;

import com.sporty.jackpot.model.Jackpot;

public interface RewardStrategy {

    RewardResult evaluate(Jackpot jackpot);
}
