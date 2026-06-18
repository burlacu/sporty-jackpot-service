package com.sporty.jackpot.reward;

public interface RandomProvider {

    /** Returns a value in [0.0, 1.0). */
    double nextDouble();
}
