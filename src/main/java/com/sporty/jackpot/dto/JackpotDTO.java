package com.sporty.jackpot.dto;

import com.sporty.jackpot.model.ContributionType;
import com.sporty.jackpot.model.RewardType;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JackpotDTO {

    private Long id;

    @NotNull(message = "Initial pool amount is required")
    private BigDecimal initialPoolAmount;

    private BigDecimal currentPoolAmount;

    @NotNull(message = "Contribution type is required")
    private ContributionType contributionType;

    @NotNull(message = "Reward type is required")
    private RewardType rewardType;

    private String configuration;
}
