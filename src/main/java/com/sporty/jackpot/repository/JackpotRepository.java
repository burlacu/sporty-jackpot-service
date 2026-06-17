package com.sporty.jackpot.repository;

import com.sporty.jackpot.model.Jackpot;
import com.sporty.jackpot.model.JackpotStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JackpotRepository extends JpaRepository<Jackpot, Long> {

    List<Jackpot> findByStatus(JackpotStatus status);
}
