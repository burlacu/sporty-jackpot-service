package com.sporty.jackpot.service.impl;

import com.sporty.jackpot.dto.ContributionRequestDTO;
import com.sporty.jackpot.dto.ContributionResponseDTO;
import com.sporty.jackpot.exception.ContributionNotFoundException;
import com.sporty.jackpot.exception.JackpotNotFoundException;
import com.sporty.jackpot.model.Jackpot;
import com.sporty.jackpot.model.JackpotContribution;
import com.sporty.jackpot.model.JackpotStatus;
import com.sporty.jackpot.repository.ContributionRepository;
import com.sporty.jackpot.repository.JackpotRepository;
import com.sporty.jackpot.service.ContributionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ContributionServiceImpl implements ContributionService {

    private final ContributionRepository contributionRepository;
    private final JackpotRepository jackpotRepository;

    @Override
    public ContributionResponseDTO addContribution(Long jackpotId, ContributionRequestDTO requestDTO) {
        Jackpot jackpot = jackpotRepository.findById(jackpotId)
                .orElseThrow(() -> new JackpotNotFoundException("Jackpot not found with id: " + jackpotId));

        if (jackpot.getStatus() != JackpotStatus.ACTIVE) {
            throw new IllegalStateException("Contributions can only be made to ACTIVE jackpots");
        }

        JackpotContribution contribution = JackpotContribution.builder()
                .jackpot(jackpot)
                .userId(requestDTO.getUserId())
                .amount(requestDTO.getAmount())
                .build();

        jackpot.setTotalAmount(jackpot.getTotalAmount().add(requestDTO.getAmount()));
        jackpotRepository.save(jackpot);

        return toDTO(contributionRepository.save(contribution));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContributionResponseDTO> getContributionsByJackpot(Long jackpotId) {
        if (!jackpotRepository.existsById(jackpotId)) {
            throw new JackpotNotFoundException("Jackpot not found with id: " + jackpotId);
        }
        return contributionRepository.findByJackpotId(jackpotId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContributionResponseDTO> getContributionsByUser(Long userId) {
        return contributionRepository.findByUserId(userId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ContributionResponseDTO getContributionById(Long id) {
        return contributionRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new ContributionNotFoundException("Contribution not found with id: " + id));
    }

    private ContributionResponseDTO toDTO(JackpotContribution contribution) {
        return ContributionResponseDTO.builder()
                .id(contribution.getId())
                .jackpotId(contribution.getJackpot().getId())
                .userId(contribution.getUserId())
                .amount(contribution.getAmount())
                .contributedAt(contribution.getContributedAt())
                .build();
    }
}
