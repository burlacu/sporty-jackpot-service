package com.sporty.jackpot.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "jackpot.reward")
@Getter
@Setter
public class TieredProbabilityProperties {

    private List<TierConfig> tieredProbabilities = new ArrayList<>();

    @Getter
    @Setter
    public static class TierConfig {

        /** Upper pool bound (exclusive) for this tier. Null means catch-all. */
        private BigDecimal threshold;

        /** Winning probability expressed as a percentage, e.g. 5 means 5%. */
        private double probabilityRate;
    }
}
