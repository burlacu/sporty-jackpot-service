package com.sporty.jackpot.strategy;

import com.sporty.jackpot.model.ContributionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ContributionStrategyFactory {

    private final PercentageContributionStrategy percentageStrategy;
    private final FixedContributionStrategy fixedStrategy;
    private final TieredContributionStrategy tieredStrategy;

    public ContributionStrategy resolve(ContributionType type) {
        return switch (type) {
            case PERCENTAGE -> percentageStrategy;
            case FIXED -> fixedStrategy;
            case TIERED -> tieredStrategy;
        };
    }
}
