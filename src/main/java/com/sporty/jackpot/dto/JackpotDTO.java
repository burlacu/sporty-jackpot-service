package com.sporty.jackpot.dto;

import com.sporty.jackpot.model.JackpotStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JackpotDTO {

    private Long id;

    @NotBlank(message = "Jackpot name is required")
    private String name;

    private BigDecimal totalAmount;

    @NotNull(message = "Jackpot status is required")
    private JackpotStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
