package com.sporty.jackpot.repository;

import com.sporty.jackpot.model.JackpotContribution;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ContributionRepositoryTest {

    @Autowired
    private ContributionRepository contributionRepository;

    @Test
    void findByJackpotId_returnsOnlyMatchingContributions() {
        contributionRepository.save(contribution(1L, 10L, 100L));
        contributionRepository.save(contribution(1L, 11L, 101L));
        contributionRepository.save(contribution(2L, 12L, 102L));

        List<JackpotContribution> result = contributionRepository.findByJackpotId(1L);

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(c -> c.getJackpotId().equals(1L));
    }

    @Test
    void findByUserId_returnsOnlyMatchingContributions() {
        contributionRepository.save(contribution(1L, 10L, 200L));
        contributionRepository.save(contribution(2L, 10L, 201L));
        contributionRepository.save(contribution(3L, 99L, 202L));

        List<JackpotContribution> result = contributionRepository.findByUserId(10L);

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(c -> c.getUserId().equals(10L));
    }

    @Test
    void findByJackpotId_noMatches_returnsEmptyList() {
        List<JackpotContribution> result = contributionRepository.findByJackpotId(999L);
        assertThat(result).isEmpty();
    }

    @Test
    void save_setsCreatedAt() {
        JackpotContribution saved = contributionRepository.save(contribution(1L, 10L, 100L));
        assertThat(saved.getCreatedAt()).isNotNull();
    }

    private JackpotContribution contribution(Long jackpotId, Long userId, Long betId) {
        return JackpotContribution.builder()
                .jackpotId(jackpotId)
                .userId(userId)
                .betId(betId)
                .stakeAmount(new BigDecimal("100.00"))
                .contributionAmount(new BigDecimal("5.00"))
                .currentJackpotAmount(new BigDecimal("1005.00"))
                .build();
    }
}
