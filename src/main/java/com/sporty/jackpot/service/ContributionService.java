package com.sporty.jackpot.service;

import com.sporty.jackpot.dto.ContributionRequestDTO;
import com.sporty.jackpot.dto.ContributionResponseDTO;

import java.util.List;

public interface ContributionService {

    ContributionResponseDTO addContribution(Long jackpotId, ContributionRequestDTO requestDTO);

    List<ContributionResponseDTO> getContributionsByJackpot(Long jackpotId);

    List<ContributionResponseDTO> getContributionsByUser(Long userId);

    ContributionResponseDTO getContributionById(Long id);
}
