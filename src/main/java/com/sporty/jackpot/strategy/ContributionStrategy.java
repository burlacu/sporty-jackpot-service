package com.sporty.jackpot.strategy;

import java.math.BigDecimal;

public interface ContributionStrategy {

    BigDecimal calculateContribution(BigDecimal betAmount, BigDecimal currentPool);
}
