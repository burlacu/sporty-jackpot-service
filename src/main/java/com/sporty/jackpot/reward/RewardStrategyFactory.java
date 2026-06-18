package com.sporty.jackpot.reward;

import com.sporty.jackpot.model.RewardType;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

/**
 * Resolves the correct {@link RewardStrategy} for a given {@link RewardType}.
 *
 * <p>Spring auto-injects all {@link RewardStrategy} beans as a map keyed by bean name.
 * Each strategy must be registered with {@code @Component("<TYPE_NAME>")} matching the
 * {@link RewardType} constant name. Adding a new strategy requires no changes here.
 */
@Component
public class RewardStrategyFactory {

    private final Map<String, RewardStrategy> strategies;

    public RewardStrategyFactory(Map<String, RewardStrategy> strategies) {
        this.strategies = strategies;
    }

    public RewardStrategy resolve(RewardType type) {
        return Optional.ofNullable(strategies.get(type.name()))
                .orElseThrow(() -> new IllegalArgumentException(
                        "No reward strategy registered for type: " + type));
    }
}
