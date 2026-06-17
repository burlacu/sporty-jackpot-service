package com.sporty.jackpot.service;

import com.sporty.jackpot.dto.JackpotDTO;
import com.sporty.jackpot.model.JackpotStatus;

import java.util.List;

public interface JackpotService {

    JackpotDTO createJackpot(JackpotDTO jackpotDTO);

    JackpotDTO getJackpotById(Long id);

    List<JackpotDTO> getAllJackpots();

    List<JackpotDTO> getJackpotsByStatus(JackpotStatus status);

    JackpotDTO updateJackpot(Long id, JackpotDTO jackpotDTO);

    void deleteJackpot(Long id);
}
