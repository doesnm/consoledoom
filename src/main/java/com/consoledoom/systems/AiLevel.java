package com.consoledoom.systems;

public enum AiLevel {
    DUMB(0, 0, 30),
    NORMAL(2, 4, 15),
    SMART(4, 8, 6),
    GODLIKE(6, 12, 0);

    public final int horizonTicks;
    public final int dangerWeight;
    public final int randomnessPercent;

    AiLevel(int horizonTicks, int dangerWeight, int randomnessPercent) {
        this.horizonTicks = horizonTicks;
        this.dangerWeight = dangerWeight;
        this.randomnessPercent = randomnessPercent;
    }
}
