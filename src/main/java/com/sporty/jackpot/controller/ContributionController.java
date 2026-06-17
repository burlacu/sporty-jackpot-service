package com.sporty.jackpot.controller;

import com.sporty.jackpot.dto.ContributionRequestDTO;
import com.sporty.jackpot.dto.ContributionResponseDTO;
import com.sporty.jackpot.service.ContributionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ContributionController {

    private final ContributionService contributionService;

    @PostMapping("/jackpots/{jackpotId}/contributions")
    public ResponseEntity<ContributionResponseDTO> addContribution(
            @PathVariable Long jackpotId,
            @Valid @RequestBody ContributionRequestDTO requestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(contributionService.addContribution(jackpotId, requestDTO));
    }

    @GetMapping("/jackpots/{jackpotId}/contributions")
    public ResponseEntity<List<ContributionResponseDTO>> getContributionsByJackpot(
            @PathVariable Long jackpotId) {
        return ResponseEntity.ok(contributionService.getContributionsByJackpot(jackpotId));
    }

    @GetMapping("/contributions/{id}")
    public ResponseEntity<ContributionResponseDTO> getContributionById(@PathVariable Long id) {
        return ResponseEntity.ok(contributionService.getContributionById(id));
    }

    @GetMapping("/users/{userId}/contributions")
    public ResponseEntity<List<ContributionResponseDTO>> getContributionsByUser(
            @PathVariable Long userId) {
        return ResponseEntity.ok(contributionService.getContributionsByUser(userId));
    }
}
