package com.sporty.jackpot.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bet {

    private Long betId;
    private Long userId;
    private Long jackpotId;
    private BigDecimal amount;
    private LocalDateTime timestamp;
}
