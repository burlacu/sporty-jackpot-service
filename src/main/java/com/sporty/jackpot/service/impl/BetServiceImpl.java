package com.sporty.jackpot.service.impl;

import com.sporty.jackpot.dto.BetAcknowledgement;
import com.sporty.jackpot.dto.BetRequest;
import com.sporty.jackpot.event.BetEvent;
import com.sporty.jackpot.model.ProcessedBet;
import com.sporty.jackpot.repository.ProcessedBetRepository;
import com.sporty.jackpot.service.BetService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class BetServiceImpl implements BetService {

    private static final Logger log = LoggerFactory.getLogger(BetServiceImpl.class);
    private static final String BET_EVENTS_TOPIC = "bet.events";

    private final ProcessedBetRepository processedBetRepository;
    private final KafkaTemplate<String, BetEvent> kafkaTemplate;

    @Override
    public BetAcknowledgement processBet(BetRequest request) {
        log.info("Received bet: betId={}, jackpotId={}", request.getBetId(), request.getJackpotId());

        try {
            processedBetRepository.save(ProcessedBet.builder().betId(request.getBetId()).build());
            processedBetRepository.flush();
        } catch (DataIntegrityViolationException e) {
            log.info("Duplicate bet ignored: betId={}, jackpotId={}", request.getBetId(), request.getJackpotId());
            return buildAck(request.getBetId(), "ALREADY_PROCESSED");
        }

        BetEvent event = BetEvent.builder()
                .betId(request.getBetId())
                .userId(request.getUserId())
                .jackpotId(request.getJackpotId())
                .amount(request.getAmount())
                .timestamp(LocalDateTime.now())
                .build();

        kafkaTemplate.send(BET_EVENTS_TOPIC, String.valueOf(request.getBetId()), event);

        log.info("Bet published to Kafka: betId={}, jackpotId={}", request.getBetId(), request.getJackpotId());
        return buildAck(request.getBetId(), "ACCEPTED");
    }

    private BetAcknowledgement buildAck(Long betId, String status) {
        return BetAcknowledgement.builder()
                .betId(betId)
                .status(status)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
