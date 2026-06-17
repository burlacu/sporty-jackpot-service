package com.sporty.jackpot.publisher;

import com.sporty.jackpot.model.Bet;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaBetPublisher implements BetPublisher {

    private static final Logger log = LoggerFactory.getLogger(KafkaBetPublisher.class);
    private static final String TOPIC = "jackpot-bets";

    private final KafkaTemplate<String, Bet> kafkaTemplate;

    @Override
    public void publish(Bet bet) {
        kafkaTemplate.send(TOPIC, String.valueOf(bet.getBetId()), bet)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish bet: betId={}, jackpotId={}, error={}",
                                bet.getBetId(), bet.getJackpotId(), ex.getMessage(), ex);
                    } else {
                        log.info("Bet published: betId={}, jackpotId={}, partition={}, offset={}",
                                bet.getBetId(), bet.getJackpotId(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    }
                });
    }
}
