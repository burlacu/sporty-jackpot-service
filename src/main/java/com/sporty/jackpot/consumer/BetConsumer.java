package com.sporty.jackpot.consumer;

import com.sporty.jackpot.model.Bet;
import com.sporty.jackpot.service.BetProcessingService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BetConsumer {

    private static final Logger log = LoggerFactory.getLogger(BetConsumer.class);

    private final BetProcessingService betProcessingService;

    @KafkaListener(topics = "jackpot-bets", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(Bet bet) {
        log.info("Consumed bet event: betId={}, jackpotId={}", bet.getBetId(), bet.getJackpotId());
        betProcessingService.process(bet);
    }
}
