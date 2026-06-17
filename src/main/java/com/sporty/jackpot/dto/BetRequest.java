package com.sporty.jackpot.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BetRequest {

    @NotNull(message = "Bet ID is required")
    private Long betId;

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Jackpot ID is required")
    private Long jackpotId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    private BigDecimal amount;
}
