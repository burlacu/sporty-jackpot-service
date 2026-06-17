package com.sporty.jackpot.service.impl;

import com.sporty.jackpot.dto.JackpotDTO;
import com.sporty.jackpot.exception.JackpotNotFoundException;
import com.sporty.jackpot.model.Jackpot;
import com.sporty.jackpot.repository.JackpotRepository;
import com.sporty.jackpot.service.JackpotService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class JackpotServiceImpl implements JackpotService {

    private final JackpotRepository jackpotRepository;

    @Override
    public JackpotDTO createJackpot(JackpotDTO jackpotDTO) {
        Jackpot jackpot = Jackpot.builder()
                .initialPoolAmount(jackpotDTO.getInitialPoolAmount())
                .currentPoolAmount(jackpotDTO.getInitialPoolAmount())
                .contributionType(jackpotDTO.getContributionType())
                .rewardType(jackpotDTO.getRewardType())
                .configuration(jackpotDTO.getConfiguration())
                .build();
        return toDTO(jackpotRepository.save(jackpot));
    }

    @Override
    @Transactional(readOnly = true)
    public JackpotDTO getJackpotById(Long id) {
        return jackpotRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new JackpotNotFoundException("Jackpot not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<JackpotDTO> getAllJackpots() {
        return jackpotRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public JackpotDTO updateJackpot(Long id, JackpotDTO jackpotDTO) {
        Jackpot jackpot = jackpotRepository.findById(id)
                .orElseThrow(() -> new JackpotNotFoundException("Jackpot not found with id: " + id));
        jackpot.setCurrentPoolAmount(jackpotDTO.getCurrentPoolAmount());
        jackpot.setContributionType(jackpotDTO.getContributionType());
        jackpot.setRewardType(jackpotDTO.getRewardType());
        jackpot.setConfiguration(jackpotDTO.getConfiguration());
        return toDTO(jackpotRepository.save(jackpot));
    }

    @Override
    public void deleteJackpot(Long id) {
        if (!jackpotRepository.existsById(id)) {
            throw new JackpotNotFoundException("Jackpot not found with id: " + id);
        }
        jackpotRepository.deleteById(id);
    }

    private JackpotDTO toDTO(Jackpot jackpot) {
        return JackpotDTO.builder()
                .id(jackpot.getId())
                .initialPoolAmount(jackpot.getInitialPoolAmount())
                .currentPoolAmount(jackpot.getCurrentPoolAmount())
                .contributionType(jackpot.getContributionType())
                .rewardType(jackpot.getRewardType())
                .configuration(jackpot.getConfiguration())
                .build();
    }
}
