package com.sporty.jackpot.strategy;

import com.sporty.jackpot.config.TieredContributionProperties;
import com.sporty.jackpot.model.ContributionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class ContributionStrategyFactoryTest {

    private ContributionStrategyFactory factory;

    @BeforeEach
    void setUp() {
        PercentageContributionStrategy percentage = new PercentageContributionStrategy(new BigDecimal("5"));
        FixedContributionStrategy fixed = new FixedContributionStrategy(new BigDecimal("10"));
        TieredContributionProperties props = new TieredContributionProperties();
        TieredContributionStrategy tiered = new TieredContributionStrategy(props);

        factory = new ContributionStrategyFactory(percentage, fixed, tiered);
    }

    @Test
    void resolve_percentage_returnsPercentageStrategy() {
        ContributionStrategy strategy = factory.resolve(ContributionType.PERCENTAGE);
        assertThat(strategy).isInstanceOf(PercentageContributionStrategy.class);
    }

    @Test
    void resolve_fixed_returnsFixedStrategy() {
        ContributionStrategy strategy = factory.resolve(ContributionType.FIXED);
        assertThat(strategy).isInstanceOf(FixedContributionStrategy.class);
    }

    @Test
    void resolve_tiered_returnsTieredStrategy() {
        ContributionStrategy strategy = factory.resolve(ContributionType.TIERED);
        assertThat(strategy).isInstanceOf(TieredContributionStrategy.class);
    }
}
