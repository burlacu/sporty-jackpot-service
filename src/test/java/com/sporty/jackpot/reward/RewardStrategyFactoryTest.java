package com.sporty.jackpot.reward;

import com.sporty.jackpot.model.RewardType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

class RewardStrategyFactoryTest {

    private RewardStrategyFactory factory;

    private final RewardStrategy fullPoolStrategy = mock(RewardStrategy.class);
    private final RewardStrategy fixedStrategy = mock(RewardStrategy.class);
    private final RewardStrategy percentageStrategy = mock(RewardStrategy.class);
    private final RewardStrategy fixedProbabilityStrategy = mock(RewardStrategy.class);
    private final RewardStrategy tieredProbabilityStrategy = mock(RewardStrategy.class);

    @BeforeEach
    void setUp() {
        factory = new RewardStrategyFactory(Map.of(
                "FULL_POOL", fullPoolStrategy,
                "FIXED", fixedStrategy,
                "PERCENTAGE", percentageStrategy,
                "FIXED_PROBABILITY", fixedProbabilityStrategy,
                "TIERED_PROBABILITY", tieredProbabilityStrategy
        ));
    }

    @Test
    void resolve_fullPool_returnsCorrectStrategy() {
        assertThat(factory.resolve(RewardType.FULL_POOL)).isSameAs(fullPoolStrategy);
    }

    @Test
    void resolve_fixed_returnsCorrectStrategy() {
        assertThat(factory.resolve(RewardType.FIXED)).isSameAs(fixedStrategy);
    }

    @Test
    void resolve_percentage_returnsCorrectStrategy() {
        assertThat(factory.resolve(RewardType.PERCENTAGE)).isSameAs(percentageStrategy);
    }

    @Test
    void resolve_fixedProbability_returnsCorrectStrategy() {
        assertThat(factory.resolve(RewardType.FIXED_PROBABILITY)).isSameAs(fixedProbabilityStrategy);
    }

    @Test
    void resolve_tieredProbability_returnsCorrectStrategy() {
        assertThat(factory.resolve(RewardType.TIERED_PROBABILITY)).isSameAs(tieredProbabilityStrategy);
    }

    @Test
    void resolve_unknownType_throwsIllegalArgumentException() {
        RewardStrategyFactory emptyFactory = new RewardStrategyFactory(Map.of());

        assertThatThrownBy(() -> emptyFactory.resolve(RewardType.FULL_POOL))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("FULL_POOL");
    }
}
