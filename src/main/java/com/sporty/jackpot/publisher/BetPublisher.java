package com.sporty.jackpot.publisher;

import com.sporty.jackpot.model.Bet;

public interface BetPublisher {

    void publish(Bet bet);
}
