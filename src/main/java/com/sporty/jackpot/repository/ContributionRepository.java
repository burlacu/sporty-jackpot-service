package com.sporty.jackpot.repository;

import com.sporty.jackpot.model.JackpotContribution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContributionRepository extends JpaRepository<JackpotContribution, Long> {

    List<JackpotContribution> findByJackpotId(Long jackpotId);

    List<JackpotContribution> findByUserId(Long userId);
}
