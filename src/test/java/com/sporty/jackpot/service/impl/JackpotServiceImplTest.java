package com.sporty.jackpot.service.impl;

import com.sporty.jackpot.dto.JackpotDTO;
import com.sporty.jackpot.exception.JackpotNotFoundException;
import com.sporty.jackpot.model.ContributionType;
import com.sporty.jackpot.model.Jackpot;
import com.sporty.jackpot.model.RewardType;
import com.sporty.jackpot.repository.JackpotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JackpotServiceImplTest {

    @Mock
    private JackpotRepository jackpotRepository;

    @InjectMocks
    private JackpotServiceImpl jackpotService;

    private Jackpot jackpot;

    @BeforeEach
    void setUp() {
        jackpot = Jackpot.builder()
                .id(1L)
                .initialPoolAmount(BigDecimal.valueOf(1000))
                .currentPoolAmount(BigDecimal.valueOf(1000))
                .contributionType(ContributionType.PERCENTAGE)
                .rewardType(RewardType.FULL_POOL)
                .build();
    }

    @Test
    void createJackpot_shouldReturnCreatedJackpotDTO() {
        JackpotDTO dto = JackpotDTO.builder()
                .initialPoolAmount(BigDecimal.valueOf(1000))
                .contributionType(ContributionType.PERCENTAGE)
                .rewardType(RewardType.FULL_POOL)
                .build();

        when(jackpotRepository.save(any(Jackpot.class))).thenReturn(jackpot);

        JackpotDTO result = jackpotService.createJackpot(dto);

        assertThat(result).isNotNull();
        assertThat(result.getContributionType()).isEqualTo(ContributionType.PERCENTAGE);
        assertThat(result.getRewardType()).isEqualTo(RewardType.FULL_POOL);
        verify(jackpotRepository, times(1)).save(any(Jackpot.class));
    }

    @Test
    void getJackpotById_whenExists_shouldReturnJackpotDTO() {
        when(jackpotRepository.findById(1L)).thenReturn(Optional.of(jackpot));

        JackpotDTO result = jackpotService.getJackpotById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getJackpotById_whenNotFound_shouldThrowException() {
        when(jackpotRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> jackpotService.getJackpotById(99L))
                .isInstanceOf(JackpotNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void getAllJackpots_shouldReturnList() {
        when(jackpotRepository.findAll()).thenReturn(List.of(jackpot));

        List<JackpotDTO> result = jackpotService.getAllJackpots();

        assertThat(result).hasSize(1);
    }

    @Test
    void deleteJackpot_whenNotExists_shouldThrowException() {
        when(jackpotRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> jackpotService.deleteJackpot(99L))
                .isInstanceOf(JackpotNotFoundException.class);
    }

    @Test
    void deleteJackpot_whenExists_shouldDeleteSuccessfully() {
        when(jackpotRepository.existsById(1L)).thenReturn(true);

        jackpotService.deleteJackpot(1L);

        verify(jackpotRepository, times(1)).deleteById(1L);
    }
}
