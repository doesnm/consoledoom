package com.consoledoom.entities.monsters;

import com.consoledoom.utils.Vec2;

import java.util.Random;

/**
 * Factory pattern:
 * Game doesn't create конкретные классы монстров напрямую.
 * Game asks factory: "give me monster for this wave".
 */
public final class MonsterFactory {

    private MonsterFactory() {}

    public enum MonsterType {
        BASIC, FAST, TANK
    }

    // Variant A: create by type (если захочешь явно выбирать)
    public static Monster create(MonsterType type, Vec2 pos) {
        return switch (type) {
            case BASIC -> new BasicMonster(pos);
            case FAST  -> new FastMonster(pos);
            case TANK  -> new TankMonster(pos);
        };
    }

    // Variant B: create by wave (самый удобный для wave survival)
    public static Monster createForWave(int wave, Random rng, Vec2 pos) {
        // базовая логика (можешь менять проценты — паттерн останется)
        if (wave >= 6 && rng.nextDouble() < 0.20) {
            return new TankMonster(pos);
        }
        if (wave >= 3 && rng.nextDouble() < 0.40) {
            return new FastMonster(pos);
        }
        return new BasicMonster(pos);
    }
}
