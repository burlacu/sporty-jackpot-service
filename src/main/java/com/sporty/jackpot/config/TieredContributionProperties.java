package com.sporty.jackpot.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "jackpot.contribution")
@Getter
@Setter
public class TieredContributionProperties {

    private List<TierConfig> tieredRates = new ArrayList<>();

    @Getter
    @Setter
    public static class TierConfig {

        /** Upper bound (exclusive) for this tier. Null means catch-all. */
        private BigDecimal threshold;

        private BigDecimal rate;
    }
}
