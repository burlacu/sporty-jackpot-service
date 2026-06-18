package com.sporty.jackpot.service.impl;

import com.sporty.jackpot.dto.BetAcknowledgement;
import com.sporty.jackpot.dto.BetRequest;
import com.sporty.jackpot.model.ProcessedBet;
import com.sporty.jackpot.publisher.BetPublisher;
import com.sporty.jackpot.repository.ProcessedBetRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BetServiceImplTest {

    @Mock private ProcessedBetRepository processedBetRepository;
    @Mock private BetPublisher betPublisher;

    @InjectMocks
    private BetServiceImpl betService;

    private final BetRequest request = BetRequest.builder()
            .betId(1L).userId(2L).jackpotId(3L).amount(new BigDecimal("50.00"))
            .build();

    @Test
    void processBet_newBet_returnsAccepted() {
        BetAcknowledgement ack = betService.processBet(request);

        assertThat(ack.getBetId()).isEqualTo(1L);
        assertThat(ack.getStatus()).isEqualTo("ACCEPTED");
        assertThat(ack.getTimestamp()).isNotNull();
    }

    @Test
    void processBet_newBet_savesProcessedBetAndPublishes() {
        betService.processBet(request);

        ArgumentCaptor<ProcessedBet> captor = ArgumentCaptor.forClass(ProcessedBet.class);
        verify(processedBetRepository).save(captor.capture());
        assertThat(captor.getValue().getBetId()).isEqualTo(1L);

        verify(betPublisher).publish(any());
    }

    @Test
    void processBet_duplicateBet_returnsAlreadyProcessed() {
        when(processedBetRepository.save(any())).thenThrow(new DataIntegrityViolationException("duplicate"));

        BetAcknowledgement ack = betService.processBet(request);

        assertThat(ack.getStatus()).isEqualTo("ALREADY_PROCESSED");
    }

    @Test
    void processBet_duplicateBet_doesNotPublish() {
        when(processedBetRepository.save(any())).thenThrow(new DataIntegrityViolationException("duplicate"));

        betService.processBet(request);

        verifyNoInteractions(betPublisher);
    }

    @Test
    void processBet_publishesCorrectBetFields() {
        betService.processBet(request);

        ArgumentCaptor<com.sporty.jackpot.model.Bet> captor =
                ArgumentCaptor.forClass(com.sporty.jackpot.model.Bet.class);
        verify(betPublisher).publish(captor.capture());

        com.sporty.jackpot.model.Bet published = captor.getValue();
        assertThat(published.getBetId()).isEqualTo(1L);
        assertThat(published.getUserId()).isEqualTo(2L);
        assertThat(published.getJackpotId()).isEqualTo(3L);
        assertThat(published.getAmount()).isEqualByComparingTo("50.00");
    }
}
