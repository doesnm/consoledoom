package com.consoledoom.systems;

public enum AiLevel {
    DUMB(0, 0, 30),
    NORMAL(2, 4, 15),
    SMART(4, 8, 6),
    GODLIKE(6, 12, 0);

    public final int horizonTicks;      // how far ahead we predict bullets
    public final int dangerWeight;      // how strongly we avoid danger
    public final int randomnessPercent; // adds noise so it’s not perfect

    AiLevel(int horizonTicks, int dangerWeight, int randomnessPercent) {
        this.horizonTicks = horizonTicks;
        this.dangerWeight = dangerWeight;
        this.randomnessPercent = randomnessPercent;
    }
}
