package com.sporty.jackpot.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JackpotRewardDTO {

    private Long id;
    private Long betId;
    private Long userId;
    private Long jackpotId;
    private BigDecimal rewardAmount;
    private LocalDateTime createdAt;
}
