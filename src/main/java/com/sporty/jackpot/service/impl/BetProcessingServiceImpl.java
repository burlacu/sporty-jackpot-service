package com.sporty.jackpot.service.impl;

import com.sporty.jackpot.dto.ContributionRequestDTO;
import com.sporty.jackpot.model.Bet;
import com.sporty.jackpot.service.BetProcessingService;
import com.sporty.jackpot.service.ContributionService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BetProcessingServiceImpl implements BetProcessingService {

    private static final Logger log = LoggerFactory.getLogger(BetProcessingServiceImpl.class);

    private final ContributionService contributionService;

    @Override
    public void process(Bet bet) {
        log.info("Processing bet: betId={}, jackpotId={}", bet.getBetId(), bet.getJackpotId());

        ContributionRequestDTO request = ContributionRequestDTO.builder()
                .betId(bet.getBetId())
                .userId(bet.getUserId())
                .stakeAmount(bet.getAmount())
                .build();

        contributionService.addContribution(bet.getJackpotId(), request);

        log.info("Contribution recorded: betId={}, jackpotId={}", bet.getBetId(), bet.getJackpotId());
    }
}
