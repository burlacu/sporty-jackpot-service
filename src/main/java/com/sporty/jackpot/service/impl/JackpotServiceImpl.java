package com.sporty.jackpot.service.impl;

import com.sporty.jackpot.dto.JackpotDTO;
import com.sporty.jackpot.exception.JackpotNotFoundException;
import com.sporty.jackpot.model.Jackpot;
import com.sporty.jackpot.model.JackpotStatus;
import com.sporty.jackpot.repository.JackpotRepository;
import com.sporty.jackpot.service.JackpotService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
                .name(jackpotDTO.getName())
                .totalAmount(BigDecimal.ZERO)
                .status(jackpotDTO.getStatus() != null ? jackpotDTO.getStatus() : JackpotStatus.ACTIVE)
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
    @Transactional(readOnly = true)
    public List<JackpotDTO> getJackpotsByStatus(JackpotStatus status) {
        return jackpotRepository.findByStatus(status).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public JackpotDTO updateJackpot(Long id, JackpotDTO jackpotDTO) {
        Jackpot jackpot = jackpotRepository.findById(id)
                .orElseThrow(() -> new JackpotNotFoundException("Jackpot not found with id: " + id));
        jackpot.setName(jackpotDTO.getName());
        jackpot.setStatus(jackpotDTO.getStatus());
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
                .name(jackpot.getName())
                .totalAmount(jackpot.getTotalAmount())
                .status(jackpot.getStatus())
                .createdAt(jackpot.getCreatedAt())
                .updatedAt(jackpot.getUpdatedAt())
                .build();
    }
}
