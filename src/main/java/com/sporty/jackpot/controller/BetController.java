package com.sporty.jackpot.controller;

import com.sporty.jackpot.dto.BetAcknowledgement;
import com.sporty.jackpot.dto.BetRequest;
import com.sporty.jackpot.service.BetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/bets")
@RequiredArgsConstructor
public class BetController {

    private final BetService betService;

    @PostMapping
    public ResponseEntity<BetAcknowledgement> receiveBet(@Valid @RequestBody BetRequest request) {
        return ResponseEntity.ok(betService.processBet(request));
    }
}
