package com.sporty.jackpot.service;

import com.sporty.jackpot.dto.BetAcknowledgement;
import com.sporty.jackpot.dto.BetRequest;

public interface BetService {

    BetAcknowledgement processBet(BetRequest request);
}
