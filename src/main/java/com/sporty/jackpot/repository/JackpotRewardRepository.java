package com.sporty.jackpot.repository;

import com.sporty.jackpot.model.JackpotReward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JackpotRewardRepository extends JpaRepository<JackpotReward, Long> {

    List<JackpotReward> findByJackpotId(Long jackpotId);

    List<JackpotReward> findByUserId(Long userId);
}
