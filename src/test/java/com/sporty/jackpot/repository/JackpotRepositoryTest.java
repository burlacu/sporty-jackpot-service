package com.sporty.jackpot.repository;

import com.sporty.jackpot.model.ContributionType;
import com.sporty.jackpot.model.Jackpot;
import com.sporty.jackpot.model.RewardType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class JackpotRepositoryTest {

    @Autowired
    private JackpotRepository jackpotRepository;

    @Test
    void save_andFindById_returnsJackpot() {
        Jackpot jackpot = jackpot("1000.00", ContributionType.PERCENTAGE, RewardType.FULL_POOL);

        Jackpot saved = jackpotRepository.save(jackpot);
        Optional<Jackpot> found = jackpotRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getInitialPoolAmount()).isEqualByComparingTo("1000.00");
        assertThat(found.get().getContributionType()).isEqualTo(ContributionType.PERCENTAGE);
        assertThat(found.get().getRewardType()).isEqualTo(RewardType.FULL_POOL);
    }

    @Test
    void save_setsCurrentPoolAmountToInitial() {
        Jackpot saved = jackpotRepository.save(jackpot("5000.00", ContributionType.FIXED, RewardType.FIXED));

        assertThat(saved.getCurrentPoolAmount()).isEqualByComparingTo("5000.00");
    }

    @Test
    void findAll_returnsAllSavedJackpots() {
        jackpotRepository.save(jackpot("1000.00", ContributionType.PERCENTAGE, RewardType.FULL_POOL));
        jackpotRepository.save(jackpot("2000.00", ContributionType.FIXED, RewardType.FIXED));

        List<Jackpot> all = jackpotRepository.findAll();

        assertThat(all).hasSize(2);
    }

    @Test
    void deleteById_removesJackpot() {
        Jackpot saved = jackpotRepository.save(jackpot("1000.00", ContributionType.PERCENTAGE, RewardType.FULL_POOL));

        jackpotRepository.deleteById(saved.getId());

        assertThat(jackpotRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    void updatePoolAmount_persistsChange() {
        Jackpot saved = jackpotRepository.save(jackpot("1000.00", ContributionType.PERCENTAGE, RewardType.FULL_POOL));

        saved.setCurrentPoolAmount(new BigDecimal("1500.00"));
        jackpotRepository.save(saved);

        Jackpot reloaded = jackpotRepository.findById(saved.getId()).orElseThrow();
        assertThat(reloaded.getCurrentPoolAmount()).isEqualByComparingTo("1500.00");
    }

    private Jackpot jackpot(String amount, ContributionType ct, RewardType rt) {
        return Jackpot.builder()
                .initialPoolAmount(new BigDecimal(amount))
                .currentPoolAmount(new BigDecimal(amount))
                .contributionType(ct)
                .rewardType(rt)
                .build();
    }
}
