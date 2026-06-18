package com.sporty.jackpot.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EvaluateRewardRequest {

    @NotNull(message = "Contribution ID is required")
    private Long contributionId;
}
