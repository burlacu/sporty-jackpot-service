package com.sporty.jackpot.repository;

import com.sporty.jackpot.model.ProcessedBet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessedBetRepository extends JpaRepository<ProcessedBet, Long> {

    boolean existsByBetId(Long betId);
}
