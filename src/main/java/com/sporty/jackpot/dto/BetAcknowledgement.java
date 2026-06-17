package com.sporty.jackpot.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BetAcknowledgement {

    private Long betId;
    private String status;
    private LocalDateTime timestamp;
}
