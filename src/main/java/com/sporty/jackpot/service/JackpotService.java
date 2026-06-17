package com.sporty.jackpot.service;

import com.sporty.jackpot.dto.JackpotDTO;

import java.util.List;

public interface JackpotService {

    JackpotDTO createJackpot(JackpotDTO jackpotDTO);

    JackpotDTO getJackpotById(Long id);

    List<JackpotDTO> getAllJackpots();

    JackpotDTO updateJackpot(Long id, JackpotDTO jackpotDTO);

    void deleteJackpot(Long id);
}
