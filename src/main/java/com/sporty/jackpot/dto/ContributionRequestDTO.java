package com.sporty.jackpot.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContributionRequestDTO {

    @NotNull(message = "Bet ID is required")
    private Long betId;

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Stake amount is required")
    @DecimalMin(value = "0.01", message = "Stake amount must be greater than zero")
    private BigDecimal stakeAmount;

    @NotNull(message = "Contribution amount is required")
    @DecimalMin(value = "0.01", message = "Contribution amount must be greater than zero")
    private BigDecimal contributionAmount;
}
