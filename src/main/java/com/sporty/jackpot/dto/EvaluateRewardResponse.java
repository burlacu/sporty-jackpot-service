package com.sporty.jackpot.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class EvaluateRewardResponse {

    private final boolean winner;
    private final BigDecimal rewardAmount;
}
