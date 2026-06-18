package com.sporty.jackpot.strategy;

import com.sporty.jackpot.model.ContributionType;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

/**
 * Resolves the correct {@link ContributionStrategy} for a given {@link ContributionType}.
 *
 * <p>Spring auto-injects all {@link ContributionStrategy} beans as a map keyed by bean name.
 * Each strategy must be registered with {@code @Component("<TYPE_NAME>")} matching the
 * {@link ContributionType} constant name. Adding a new strategy requires no changes here.
 */
@Component
public class ContributionStrategyFactory {

    private final Map<String, ContributionStrategy> strategies;

    public ContributionStrategyFactory(Map<String, ContributionStrategy> strategies) {
        this.strategies = strategies;
    }

    public ContributionStrategy resolve(ContributionType type) {
        return Optional.ofNullable(strategies.get(type.name()))
                .orElseThrow(() -> new IllegalArgumentException(
                        "No contribution strategy registered for type: " + type));
    }
}
