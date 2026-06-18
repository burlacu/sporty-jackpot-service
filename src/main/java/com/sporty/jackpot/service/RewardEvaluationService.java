package com.sporty.jackpot.service;

import com.sporty.jackpot.dto.EvaluateRewardRequest;
import com.sporty.jackpot.dto.EvaluateRewardResponse;

public interface RewardEvaluationService {

    EvaluateRewardResponse evaluate(EvaluateRewardRequest request);
}
