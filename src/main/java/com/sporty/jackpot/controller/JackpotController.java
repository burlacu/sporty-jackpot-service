package com.sporty.jackpot.controller;

import com.sporty.jackpot.dto.JackpotDTO;
import com.sporty.jackpot.service.JackpotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/jackpots")
@RequiredArgsConstructor
public class JackpotController {

    private final JackpotService jackpotService;

    @PostMapping
    public ResponseEntity<JackpotDTO> createJackpot(@Valid @RequestBody JackpotDTO jackpotDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(jackpotService.createJackpot(jackpotDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<JackpotDTO> getJackpotById(@PathVariable Long id) {
        return ResponseEntity.ok(jackpotService.getJackpotById(id));
    }

    @GetMapping
    public ResponseEntity<List<JackpotDTO>> getAllJackpots() {
        return ResponseEntity.ok(jackpotService.getAllJackpots());
    }

    @PutMapping("/{id}")
    public ResponseEntity<JackpotDTO> updateJackpot(
            @PathVariable Long id,
            @Valid @RequestBody JackpotDTO jackpotDTO) {
        return ResponseEntity.ok(jackpotService.updateJackpot(id, jackpotDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJackpot(@PathVariable Long id) {
        jackpotService.deleteJackpot(id);
        return ResponseEntity.noContent().build();
    }
}
