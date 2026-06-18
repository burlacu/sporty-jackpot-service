package com.sporty.jackpot.consumer;

import com.sporty.jackpot.model.Bet;
import com.sporty.jackpot.service.BetProcessingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class BetConsumerTest {

    @Mock
    private BetProcessingService betProcessingService;

    @InjectMocks
    private BetConsumer betConsumer;

    @Test
    void consume_delegatesToBetProcessingService() {
        Bet bet = Bet.builder()
                .betId(1L)
                .userId(2L)
                .jackpotId(3L)
                .amount(new BigDecimal("50.00"))
                .build();

        betConsumer.consume(bet);

        verify(betProcessingService).process(bet);
    }

    @Test
    void consume_containsNoBusinessLogic() {
        Bet bet = Bet.builder()
                .betId(1L)
                .userId(2L)
                .jackpotId(3L)
                .amount(new BigDecimal("50.00"))
                .build();

        betConsumer.consume(bet);

        // only one interaction — process() — confirming no extra logic in the consumer
        verify(betProcessingService).process(bet);
        verifyNoMoreInteractions(betProcessingService);
    }
}
