package com.sporty.jackpot.reward;

import java.math.BigDecimal;

public record RewardResult(boolean triggered, BigDecimal rewardAmount) {

    public static RewardResult win(BigDecimal rewardAmount) {
        return new RewardResult(true, rewardAmount);
    }

    public static RewardResult miss() {
        return new RewardResult(false, BigDecimal.ZERO);
    }
}
