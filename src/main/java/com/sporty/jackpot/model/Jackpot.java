package com.sporty.jackpot.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "jackpots")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Jackpot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal initialPoolAmount;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal currentPoolAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContributionType contributionType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RewardType rewardType;

    @Column(columnDefinition = "TEXT")
    private String configuration;
}
