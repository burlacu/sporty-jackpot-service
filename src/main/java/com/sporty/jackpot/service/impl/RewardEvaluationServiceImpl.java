package com.sporty.jackpot.service.impl;

import com.sporty.jackpot.dto.EvaluateRewardRequest;
import com.sporty.jackpot.dto.EvaluateRewardResponse;
import com.sporty.jackpot.exception.ContributionNotFoundException;
import com.sporty.jackpot.exception.JackpotNotFoundException;
import com.sporty.jackpot.model.Jackpot;
import com.sporty.jackpot.model.JackpotContribution;
import com.sporty.jackpot.model.JackpotReward;
import com.sporty.jackpot.repository.ContributionRepository;
import com.sporty.jackpot.repository.JackpotRepository;
import com.sporty.jackpot.repository.JackpotRewardRepository;
import com.sporty.jackpot.reward.RewardResult;
import com.sporty.jackpot.reward.RewardStrategy;
import com.sporty.jackpot.reward.RewardStrategyFactory;
import com.sporty.jackpot.service.RewardEvaluationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RewardEvaluationServiceImpl implements RewardEvaluationService {

    private static final Logger log = LoggerFactory.getLogger(RewardEvaluationServiceImpl.class);

    private final ContributionRepository contributionRepository;
    private final JackpotRepository jackpotRepository;
    private final JackpotRewardRepository jackpotRewardRepository;
    private final RewardStrategyFactory rewardStrategyFactory;

    @Override
    public EvaluateRewardResponse evaluate(EvaluateRewardRequest request) {
        JackpotContribution contribution = contributionRepository.findById(request.getContributionId())
                .orElseThrow(() -> new ContributionNotFoundException(
                        "Contribution not found with id: " + request.getContributionId()));

        Jackpot jackpot = jackpotRepository.findById(contribution.getJackpotId())
                .orElseThrow(() -> new JackpotNotFoundException(
                        "Jackpot not found with id: " + contribution.getJackpotId()));

        RewardStrategy strategy = rewardStrategyFactory.resolve(jackpot.getRewardType());
        RewardResult result = strategy.evaluate(jackpot);

        log.info("Reward evaluation: jackpotId={}, contributionId={}, triggered={}",
                jackpot.getId(), contribution.getId(), result.triggered());

        if (result.triggered()) {
            jackpotRewardRepository.save(JackpotReward.builder()
                    .betId(contribution.getBetId())
                    .userId(contribution.getUserId())
                    .jackpotId(jackpot.getId())
                    .rewardAmount(result.rewardAmount())
                    .build());

            jackpot.setCurrentPoolAmount(jackpot.getInitialPoolAmount());
            jackpotRepository.save(jackpot);

            log.info("Jackpot won: jackpotId={}, userId={}, amount={}",
                    jackpot.getId(), contribution.getUserId(), result.rewardAmount());
        }

        return new EvaluateRewardResponse(result.triggered(), result.rewardAmount());
    }
}
