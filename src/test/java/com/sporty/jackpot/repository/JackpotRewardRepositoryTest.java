package com.sporty.jackpot.repository;

import com.sporty.jackpot.model.JackpotReward;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class JackpotRewardRepositoryTest {

    @Autowired
    private JackpotRewardRepository jackpotRewardRepository;

    @Test
    void findByJackpotId_returnsOnlyMatchingRewards() {
        jackpotRewardRepository.save(reward(1L, 10L));
        jackpotRewardRepository.save(reward(1L, 11L));
        jackpotRewardRepository.save(reward(2L, 12L));

        List<JackpotReward> result = jackpotRewardRepository.findByJackpotId(1L);

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(r -> r.getJackpotId().equals(1L));
    }

    @Test
    void findByUserId_returnsOnlyMatchingRewards() {
        jackpotRewardRepository.save(reward(1L, 10L));
        jackpotRewardRepository.save(reward(2L, 10L));
        jackpotRewardRepository.save(reward(3L, 99L));

        List<JackpotReward> result = jackpotRewardRepository.findByUserId(10L);

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(r -> r.getUserId().equals(10L));
    }

    @Test
    void findByJackpotId_noMatches_returnsEmptyList() {
        List<JackpotReward> result = jackpotRewardRepository.findByJackpotId(999L);
        assertThat(result).isEmpty();
    }

    @Test
    void save_setsCreatedAt() {
        JackpotReward saved = jackpotRewardRepository.save(reward(1L, 10L));
        assertThat(saved.getCreatedAt()).isNotNull();
    }

    private JackpotReward reward(Long jackpotId, Long userId) {
        return JackpotReward.builder()
                .jackpotId(jackpotId)
                .userId(userId)
                .betId(100L)
                .rewardAmount(new BigDecimal("5000.00"))
                .build();
    }
}
