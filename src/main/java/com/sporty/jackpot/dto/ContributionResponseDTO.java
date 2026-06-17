package com.sporty.jackpot.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContributionResponseDTO {

    private Long id;
    private Long jackpotId;
    private Long userId;
    private BigDecimal amount;
    private LocalDateTime contributedAt;
}
